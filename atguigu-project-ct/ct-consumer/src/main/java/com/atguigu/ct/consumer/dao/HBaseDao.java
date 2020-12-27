package com.atguigu.ct.consumer.dao;

import com.atguigu.ct.common.bean.BaseDao;
import com.atguigu.ct.common.constant.Names;
import com.atguigu.ct.common.constant.ValueConstant;
import com.atguigu.ct.consumer.bean.CallLog;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * @author springdu
 * @create 2020/12/26 16:30
 * @description TODO Hbase数据访问对象
 */
public class HBaseDao extends BaseDao {
    /**
     * 初始化
     */
    public void init() throws Exception{
        start();

        createNamespaceNX(Names.NAMESPACE.getValue());

        createTableXX(Names.TABLE.getValue(), ValueConstant.REGION_COUNT, Names.CF_CALLER.getValue());

        end();
    }

    /**
     * 插入对象
     * @param callLog
     * @throws Exception
     */
    public void insertData(CallLog callLog) throws Exception {
        callLog.setRowKey(genRegionNum(callLog.getCall1(), callLog.getCallTime())
                + "_" + callLog.getCall1()
                + "_" + callLog.getCallTime()
                + "_" + callLog.getCall2()
                + "_" + callLog.getDuration());

        putData(callLog);
    }


    /**
     * 插入数据
     * @param value
     */
    public void insertData(String value) throws Exception{

        // 将通话日志保存到Hbase表中

        // 1. 获取通话日志数据
        String[] values = value.split("\t");
        String call1 = values[0];
        String call2 = values[1];
        String callTime = values[2];
        String duration = values[3];

        // 2. 创建数据对象

        // rowKey设计
        // 1> 长度原则
        //      最大值64KB，推荐长度为10 ~ 100byte
        //      最好是8的倍数，能短则短，rowKey如果太长会影响性能
        // 2> 唯一原则：rowKey应该具备唯一性
        // 3> 散列原则
        //      3-1> 盐值散列：不能使用时间戳直接作为rowKey
        //           在rowKey前增加随机数
        //      3-2> 字符串反转：1312312334342，1312312334345
        //            电话号码：133 + 0123 + 4567（运营商编码+地区编码+用户编码）
        //      3-3> 计算分区号：HashMap

        // rowKey = regionNum + call1 + time + call2 + duration
        String rowKey = genRegionNum(call1, callTime) + "_" + call1 + "_" + callTime + "_" + call2 + "_" + duration;

        Put put = new Put(Bytes.toBytes(rowKey));

        byte[] family = Bytes.toBytes(Names.CF_CALLER.getValue());

        put.addColumn(family, Bytes.toBytes("call1"), Bytes.toBytes(call1));
        put.addColumn(family, Bytes.toBytes("call2"), Bytes.toBytes(call2));
        put.addColumn(family, Bytes.toBytes("callTime"), Bytes.toBytes(callTime));
        put.addColumn(family, Bytes.toBytes("duration"), Bytes.toBytes(duration));

        // 3. 保存数据
        putData(Names.TABLE.getValue(), put);
    }
}
