package com.atguigu.ct.common.bean;

import java.io.Closeable;

/**
 * @author springdu
 * @create 2020/12/26 15:46
 * @description TODO 消费者接口
 */
public interface Consumer extends Closeable {
    /**
     * 消费数据
     */
    public void consume();
}
