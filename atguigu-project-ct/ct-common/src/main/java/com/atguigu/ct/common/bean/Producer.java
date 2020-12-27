package com.atguigu.ct.common.bean;

import java.io.Closeable;

/**
 * @author springdu
 * @create 2020/12/24 15:40
 * @description 生产者接口
 */
public interface Producer extends Closeable {

    /**
     * 数据输入
     * @param in
     */
    public void setIn(DataIn in);

    /**
     * 数据输出
     * @param out
     */
    public void setOut(DataOut out);

    /**
     * 生产数据
     */
    public void produce();
}
