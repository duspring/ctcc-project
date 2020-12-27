package com.atguigu.ct.producer.bean;

import com.atguigu.ct.common.bean.DataIn;
import com.atguigu.ct.common.bean.DataOut;
import com.atguigu.ct.common.bean.Producer;
import com.atguigu.ct.common.util.DateUtil;
import com.atguigu.ct.common.util.NumberUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author springdu
 * @create 2020/12/24 16:01
 */
public class LocalFileProducer implements Producer {

    private DataIn in;
    private DataOut out;
    private volatile boolean flg = true;

    @Override
    public void setIn(DataIn in) {
        this.in = in;
    }

    @Override
    public void setOut(DataOut out) {
        this.out = out;
    }

    /**
     * 生产数据
     */
    @Override
    public void produce() {


        try {
            // 读取通讯录的数据
            List<Contract> contacts = in.read(Contract.class);
//            for (Contract contact : contacts) {
//                System.out.println(contact);
//            }
             while (flg) {

                // 从通讯录中随机查找2个电话号码（主叫，被叫）
                int call1Index = new Random().nextInt(contacts.size());
                int call2Index;
                while (true) {
                    call2Index = new Random().nextInt(contacts.size());
                    if (call1Index != call2Index) {
                        break;
                    }
                }

                Contract call1 = contacts.get(call1Index);
                Contract call2 = contacts.get(call2Index);

                // 生成随机的通话时间
                String startDate = "20180101000000";
                String endDate = "20190101000000";

                long startTime = DateUtil.parse(startDate, "yyyyMMddHHmmss").getTime();

                long endTime = DateUtil.parse(endDate, "yyyyMMddHHmmss").getTime();

                // 通话时间
                long callTime = startTime + (long)((endTime - startTime) * Math.random());
                String callTimeString = DateUtil.format(new Date(callTime), "yyyyMMddHHmmss");

                // 生成随机的通话时长
                String duration = NumberUtil.format(new Random().nextInt(3000), 4);

//                String durationString = "" + duration;
//                for (int i = 0; i< 4 - durationString.length(); i++) {
//                    durationString += "0";
//                    durationString = "0" + durationString;
//                }

                // 生成通话记录
                CallLog callLog = new CallLog(call1.getTel(), call2.getTel(), callTimeString, duration);

                 System.out.println(callLog);
                // 将通话记录刷写到数据文件中
                out.write(callLog);

                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭生产者
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (in != null) {
            in.close();
        }

        if (out != null) {
            out.close();
        }
    }
}
