package com.atguigu.ct.web.service;

import com.atguigu.ct.web.bean.Calllog;

import java.util.List;

/**
 * @author springdu
 * @create 2020/12/29 19:59
 * @description TODO
 */
public interface CallLogService {
    List<Calllog> queryMonthDatas(String tel, String calltime);
}
