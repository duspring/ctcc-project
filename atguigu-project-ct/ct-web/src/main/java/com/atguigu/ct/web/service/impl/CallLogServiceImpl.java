package com.atguigu.ct.web.service.impl;

import com.atguigu.ct.web.bean.Calllog;
import com.atguigu.ct.web.dao.CallLogDao;
import com.atguigu.ct.web.service.CallLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author springdu
 * @create 2020/12/29 20:00
 * @description TODO 通话日志服务对象
 */
@Service
public class CallLogServiceImpl implements CallLogService {

    @Autowired
    private CallLogDao callLogDao;

    /**
     * 查询用户指定时间的通话统计信息
     * @param tel
     * @param calltime
     * @return
     */
    @Override
    public List<Calllog> queryMonthDatas(String tel, String calltime) {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tel", tel);

        if (calltime.length() > 4) {
            calltime = calltime.substring(0, 4);
        }
        paramMap.put("year", calltime);
        return callLogDao.queryMonthDatas(paramMap);
    }
}
