package com.atguigu.ct.producer.io;

import com.atguigu.ct.common.bean.DataOut;

import java.io.*;

/**
 * @author springdu
 * @create 2020/12/25 17:30
 * @description 本地文件数据输出
 */
public class LocalFileDataOut implements DataOut {

    private PrintWriter printWriter = null;

    public LocalFileDataOut(String path) {
        setPath(path);
    }

    @Override
    public void setPath(String path) {
        try {
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Object data) throws Exception {
        write(data.toString());
    }

    /**
     * 将数据字符串生成到文件中
     * @param data
     * @throws Exception
     */
    @Override
    public void write(String data) throws Exception {
        printWriter.println(data);
        printWriter.flush();
    }

    /**
     * 释放资源
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}
