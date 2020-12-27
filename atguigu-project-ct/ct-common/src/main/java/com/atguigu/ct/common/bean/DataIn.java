package com.atguigu.ct.common.bean;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * @author springdu
 * @create 2020/12/24 15:43
 * @description 数据输入接口
 */
public interface DataIn extends Closeable {
    public void setPath(String path);

    public Object read() throws IOException;

    public <T extends Data> List<T> read(Class<T> clazz) throws IOException;
}
