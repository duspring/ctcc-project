package com.atguigu.ct.consumer;

import com.atguigu.ct.common.bean.Consumer;
import com.atguigu.ct.consumer.bean.CallLogConsumer;

import java.io.IOException;

/**
 * @author springdu
 * @create 2020/12/26 15:43
 * @description TODO 启动消费者
 *
 * 使用Kafka消费者获取Flume采集的数据
 *
 * 将数据存储到Hbase中去
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception {

        // 创建消费者
        Consumer consumer = new CallLogConsumer();

        // 消费数据
        consumer.consume();

        // 关闭资源
        consumer.close();

    }
}
