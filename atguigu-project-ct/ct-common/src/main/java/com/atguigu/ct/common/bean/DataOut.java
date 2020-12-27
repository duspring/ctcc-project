package com.atguigu.ct.common.bean;

import java.io.Closeable;

/**
 * @author springdu
 * @create 2020/12/24 15:44
 * @description 数据输出接口
 */
public interface DataOut extends Closeable {
    public void setPath(String path);

    public void write(Object data) throws Exception;

    public void write(String data) throws Exception;
}
