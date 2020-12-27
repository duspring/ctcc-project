package com.atguigu.ct.producer;

import com.atguigu.ct.common.bean.Producer;
import com.atguigu.ct.producer.bean.LocalFileProducer;
import com.atguigu.ct.producer.io.LocalFileDataIn;
import com.atguigu.ct.producer.io.LocalFileDataOut;

import java.io.IOException;

/**
 * @author springdu
 * @create 2020/12/24 15:58
 * 启动对象
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.out.println("系统的参数不正确，请按照指定格式传递： java -jar Produce.jar path1 path2 ");
            System.exit(1);
        }

        // 构建生产者对象
        Producer producer = new LocalFileProducer();

//        producer.setIn(new LocalFileDataIn("E:\\尚硅谷大数据学科——最新发布\\尚硅谷大数据技术之电信客服综合案例\\2.资料\\2.资料\\辅助文档\\contact.log"));
//
//        producer.setOut(new LocalFileDataOut("C:\\Users\\ll\\Desktop\\call.log"));

        producer.setIn(new LocalFileDataIn(args[0]));

        producer.setOut(new LocalFileDataOut(args[1]));

        // 生产数据
        producer.produce();

        // 关闭生产者对象
        producer.close();

    }
}
