package com.atguigu.ct.web.dao;

import com.atguigu.ct.web.bean.Calllog;

import java.util.List;
import java.util.Map;

/**
 * @author springdu
 * @create 2020/12/29 17:54
 * @description TODO 通话日志数据访问对象
 */
public interface CallLogDao {
    List<Calllog> queryMonthDatas(Map<String, Object> paramMap);
}
