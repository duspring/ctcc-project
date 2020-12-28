package com.atguigu.ct.consumer.bean;

import com.atguigu.ct.common.bean.Consumer;
import com.atguigu.ct.common.constant.Names;
import com.atguigu.ct.consumer.dao.HBaseDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author springdu
 * @create 2020/12/26 15:48
 * @description TODO 通话日志消费者对象
 */
public class CallLogConsumer implements Consumer {
    /**
     * 消费数据
     */
    public void consume() {

        try {
            // 创建配置对象
            Properties prop = new Properties();
//            prop.load(CallLogConsumer.class.getResourceAsStream("consumer.properties"));
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("consumer.properties"));


//        prop.setProperty("bootstrap.servers", "hadoop102:9092");
//        prop.setProperty("group.id", "consumer-group");
//        prop.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        prop.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
//        //prop.setProperty("auto.offset.reset", "latest");
//        prop.setProperty("enable.auto.commit", "true");
//        prop.setProperty("auto.commit.interval.ms", "1000");

            // 获取Flume采集的数据
            KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(prop);

            // 关注主题
            consumer.subscribe(Arrays.asList(Names.TOPIC.getValue()));

            // Hbase数据访问对象
            HBaseDao hBaseDao = new HBaseDao();
            // 初始化
            hBaseDao.init();

            // 消费数据
            while (true) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(100);
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    System.out.println(consumerRecord.value());
                    // 插入数据
                    // 方式一
                    hBaseDao.insertData(consumerRecord.value());

                    // 方式二
                    // CallLog callLog = new CallLog(consumerRecord.value());
                    // hBaseDao.insertData(callLog);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭资源
     * @throws IOException
     */
    public void close() throws IOException {

    }
}
