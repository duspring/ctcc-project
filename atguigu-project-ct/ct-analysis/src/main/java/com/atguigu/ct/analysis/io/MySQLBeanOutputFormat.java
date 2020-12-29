package com.atguigu.ct.analysis.io;

import com.atguigu.ct.analysis.kv.AnalysisKey;
import com.atguigu.ct.analysis.kv.AnalysisValue;
import com.atguigu.ct.common.util.JDBCUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author springdu
 * @create 2020/12/29 9:58
 * @description TODO MySQL数据格式化输出对象
 */
public class MySQLBeanOutputFormat extends OutputFormat<AnalysisKey, AnalysisValue> {

    protected static class MySQLRecordWriter extends RecordWriter<AnalysisKey, AnalysisValue> {

        private Connection connection = null;
        private Map<String, Integer> userMap = new HashMap<>();
        private Map<String, Integer> dateMap = new HashMap<>();

        public MySQLRecordWriter() {
            // 获取资源
            connection = JDBCUtil.getConnection();
            // 读取用户，时间数据
            PreparedStatement pstat = null;
            ResultSet rs = null;

            try {
                String queryUserSql = "select id, tel from ct_user";
                pstat = connection.prepareStatement(queryUserSql);
                rs = pstat.executeQuery();
                while (rs.next()) {
                    Integer id = rs.getInt(1);
                    String tel = rs.getString(2);
                    userMap.put(tel, id);
                }

                rs.close();

                String queryDateSql = "select id, year, month, day from ct_date";
                pstat = connection.prepareStatement(queryDateSql);
                rs = pstat.executeQuery();
                while (rs.next()) {
                    Integer id = rs.getInt(1);
                    String year = rs.getString(2);
                    String month = rs.getString(3);
                    if (month.length() == 1) {
                        month = "0" + month;
                    }
                    String day = rs.getString(4);
                    if (day.length() == 1) {
                        day = "0" + day;
                    }

                    dateMap.put(year + month + day, id);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                if (pstat != null) {
                    try {
                        pstat.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        /**
         * 输出数据
         * @param key
         * @param value
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void write(AnalysisKey key, AnalysisValue value) throws IOException, InterruptedException {

//            String[] values = value.toString().split("_");
//            String sumCall = values[0];
//            String sumDuration = values[1];

            PreparedStatement pstat = null;
            try {
                String insertSQL = "insert into ct_call (telid, dateid, sumcall, sumduration) values ( ?, ?, ? ,? )";
                pstat = connection.prepareStatement(insertSQL);

//                String k = key.toString();
//                String[] ks = k.split("_");
//
//                String tel = ks[0];
//                String date = ks[1];

                pstat.setInt(1, userMap.get(key.getTel()));
                pstat.setInt(2, dateMap.get(key.getDate()));
                pstat.setInt(3, Integer.parseInt(value.getSumCall()));
                pstat.setInt(4, Integer.parseInt(value.getSumDuration()));

                pstat.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (pstat != null) {
                    try {
                        pstat.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * 释放资源
         * @param taskAttemptContext
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void close(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public RecordWriter<AnalysisKey, AnalysisValue> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        return new MySQLRecordWriter();
    }

    @Override
    public void checkOutputSpecs(JobContext jobContext) throws IOException, InterruptedException {

    }


    private FileOutputCommitter committer = null;

    public static Path getOutputPath(JobContext job) {
        String name = job.getConfiguration().get("mapreduce.output.fileoutputformat.outputdir");
        return name == null ? null : new Path(name);
    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException, InterruptedException {
        if (this.committer == null) {
            Path output = getOutputPath(context);
            this.committer = new FileOutputCommitter(output, context);
        }

        return this.committer;
    }
}
