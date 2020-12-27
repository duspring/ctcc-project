package com.atguigu.ct.common.bean;

/**
 * @author springdu
 * @create 2020/12/24 15:47
 */
public abstract class Data implements Val {

    public String content;


    @Override
    public void setValue(Object val) {
        content = (String) val;
    }

    @Override
    public String getValue() {
        return content;
    }
}
