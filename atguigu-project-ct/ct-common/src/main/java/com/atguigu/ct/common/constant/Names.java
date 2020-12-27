package com.atguigu.ct.common.constant;

import com.atguigu.ct.common.bean.Val;

/**
 * @author springdu
 * @create 2020/12/24 15:54
 * @description 名称常量枚举类
 */
public enum Names implements Val {
    NAMESPACE("ct")
    ,TABLE("ct:calllog")
    ,CF_CALLER("caller")
    ,CF_INFO("info")
    ,TOPIC("callLog");

    private String name;

    private Names(String name) {
        this.name = name;
    }

    @Override
    public void setValue(Object val) {
        this.name = (String) val;
    }

    @Override
    public String getValue() {
        return name;
    }
}
