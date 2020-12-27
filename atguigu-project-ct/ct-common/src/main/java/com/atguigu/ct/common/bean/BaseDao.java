package com.atguigu.ct.common.bean;

import com.atguigu.ct.common.api.Column;
import com.atguigu.ct.common.api.RowKey;
import com.atguigu.ct.common.api.TableRef;
import com.atguigu.ct.common.constant.Names;
import com.atguigu.ct.common.constant.ValueConstant;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author springdu
 * @create 2020/12/26 16:29
 * @description TODO 基础的数据访问对象
 */
public abstract class BaseDao {

    private ThreadLocal<Connection> connHolder = new ThreadLocal<Connection>();
    private ThreadLocal<Admin> adminHolder = new ThreadLocal<Admin>();

    protected void start() throws Exception {
        getConnection();
        getAdmin();
    }

    protected void end() throws Exception {
        Admin admin = getAdmin();
        if (admin != null) {
            admin.close();
            adminHolder.remove();
        }

        Connection conn = getConnection();
        if (conn != null) {
            conn.close();
            connHolder.remove();
        }
    }

    /**
     * 创建表，如果表已经存在，那么删除后再创建新的
     * @param tName
     * @param families
     */
    protected void createTableXX(String tName, String... families) throws Exception {
        /*Admin admin = getAdmin();

        TableName tableName = TableName.valueOf(tName);

        if (admin.tableExists(tableName)) {
            // 表存在，删除
            deleteTable(tName);
        }

        // 创建表
        createTable(tName, null, families);*/

        createTableXX(tName, null, families);
    }

    protected void createTableXX(String tName, Integer regionCount, String... families) throws Exception {
        Admin admin = getAdmin();

        TableName tableName = TableName.valueOf(tName);

        if (admin.tableExists(tableName)) {
            // 表存在，删除
            deleteTable(tName);
        }

        // 创建表
        createTable(tName, regionCount, families);
    }

    /**
     * 创建表
     * @param tName 表名
     * @param families 列簇
     * @throws Exception
     */
    private void createTable(String tName, Integer regionCount, String... families) throws Exception {
        Admin admin = getAdmin();
        TableName tableName = TableName.valueOf(tName);

        HTableDescriptor tableDescriptor =
                new HTableDescriptor(tableName);

        if (families == null || families.length == 0) {
            families = new String[1];
            families[0] = Names.CF_INFO.getValue();
        }

        for (String family : families) {
            HColumnDescriptor columnDescriptor =
                    new HColumnDescriptor(family);
            tableDescriptor.addFamily(columnDescriptor);
        }

        // 增加预分区
        if (regionCount == null || regionCount <= 1) {
            admin.createTable(tableDescriptor);
        } else {
            // 分区键
            byte[][] splitKeys = genSplitKeys(regionCount);
            admin.createTable(tableDescriptor, splitKeys);
        }

    }

    /**
     * 计算分区号
     * @param tel
     * @param date
     * @return
     */
    protected int genRegionNum(String tel, String date) {

        // 13301234567
        String userCode = tel.substring(tel.length() - 4);
        // 20181010120000
        String yearMonth = date.substring(0, 6);

        int userCodeHash = userCode.hashCode();
        int yearMonthHash = yearMonth.hashCode();

        // crc校验采用异或算法, hash
        int crc = Math.abs(userCodeHash ^ yearMonthHash);

        // 取模
        int regionNum = crc % ValueConstant.REGION_COUNT;

        return regionNum;

    }

    /*public static void main(String[] args) {
        // 同一个电话号码 同一年同一月在同一个分区中
//        System.out.println(genRegionNum("13301010102", "20181010120000")); // 5
//        System.out.println(genRegionNum("18801011102", "20180410120000")); // 3
    }*/

    /**
     * 生成分区键
     * @param regionCount
     * @return
     */
    private byte[][] genSplitKeys(Integer regionCount) {

        int splitKeyCount = regionCount - 1;
        byte[][] bs = new byte[splitKeyCount][];
        // 0|,1|,2|,3|,4|
        // 0000000
        // 000111
        // 11000
        // 223232
        // a|b|c|d|e
        // (-∞, 0|), [0|,1|), [1|, +∞)
        List<byte[]> bsList = new ArrayList<>();
        for (int i = 0; i < splitKeyCount; i++ ) {
            String splitKey = i + "|";
//            System.out.println(splitKey);
            bsList.add(Bytes.toBytes(splitKey));
        }

        bsList.toArray(bs);

        return bs;
    }

    /**
     * 增加对象，自动封装数据，将对象数据直接保存到Hbase中去
     * @param obj
     * @throws Exception
     */
    protected void putData(Object obj) throws Exception {

        // 反射
        Class clazz = obj.getClass();
        TableRef tableRef = (TableRef) clazz.getAnnotation(TableRef.class);
        // table
        String tableName = tableRef.value();

        Field[] fs = clazz.getDeclaredFields();
        String stringRowKey = "";
        for (Field f : fs) {
            RowKey rowKey = f.getAnnotation(RowKey.class);
            if (rowKey != null) {
                f.setAccessible(true);
                stringRowKey = (String) f.get(obj);
                break;
            }

        }

        Connection conn = getConnection();
        Table table = conn.getTable(TableName.valueOf(tableName));
        // put
        Put put = new Put(Bytes.toBytes(stringRowKey));

        for (Field f : fs) {
            Column column = f.getAnnotation(Column.class);
            if (column != null) {
                String family = column.family();
                String colName = column.column();
                if (colName == null || "".equals(colName)) {
                    colName = f.getName();
                }
                f.setAccessible(true);
                String value = (String) f.get(obj);

                put.addColumn(Bytes.toBytes(family), Bytes.toBytes(colName), Bytes.toBytes(value));
            }
        }

        // 增加数据
        table.put(put);

        // 关闭表
        table.close();
    }

    /**
     *
     * @param tName
     * @param put
     * @throws Exception
     */
    protected void putData(String tName, Put put) throws Exception {

        // 获取表对象
        Connection conn = getConnection();
        Table table = conn.getTable(TableName.valueOf(tName));

        // 增加数据
        table.put(put);

        // 关闭表
        table.close();
    }

    /**
     * 删除表
     * @param tName 表名
     * @throws Exception
     */
    protected void deleteTable(String tName) throws Exception {
        TableName tableName = TableName.valueOf(tName);
        Admin admin = getAdmin();
        admin.disableTable(tableName);
        admin.deleteTable(tableName);
    }

    /**
     * 创建命名空间，如果命名空间已经存在，不需要创建，否则，创建新的
     * @param namespace
     */
    protected void createNamespaceNX(String namespace) throws Exception {
        Admin admin = getAdmin();

        try {
            admin.getNamespaceDescriptor(namespace);
        } catch (NamespaceNotFoundException e) {
            // e.printStackTrace();

            NamespaceDescriptor namespaceDescriptor =
                    NamespaceDescriptor.create(namespace).build();

            admin.createNamespace(namespaceDescriptor);
        }
    }

    /**
     * 获取Admin连接对象
     * @return
     */
    protected synchronized Admin getAdmin() throws Exception {
        Admin admin = adminHolder.get();
        if (admin == null) {
            admin = getConnection().getAdmin();
            adminHolder.set(admin);
        }

        return admin;
    }

    /**
     * 获取连接对象
     * @return
     */
    protected synchronized Connection getConnection() throws Exception{
        Connection conn = connHolder.get();
        if (conn == null) {
            Configuration conf = HBaseConfiguration.create();
            conn = ConnectionFactory.createConnection(conf);
            connHolder.set(conn);
        }

        return conn;
    }

//    public static void main(String[] args) {
//        System.out.println(genSplitKeys(6));
//    }
}
