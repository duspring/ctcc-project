package com.atguigu.ct.web.controller;

import com.atguigu.ct.web.bean.Calllog;
import com.atguigu.ct.web.service.CallLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author springdu
 * @create 2020/12/29 20:01
 * @description TODO 通话日志的控制器对象
 */
@Controller
public class CallLogController {
    @Autowired
    private CallLogService callLogService;

    @RequestMapping("/query")
    public String query() {
        return "query";
    }

    // Object ==> String ==> User@123123
    // Object ==> json ==> String
    @ResponseBody
    @RequestMapping("/view1")
    public Object view1() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("username","zhangsan" );
        dataMap.put("age","20" );

        return dataMap;
    }

    @RequestMapping("/view")
    public String view(String tel, String calltime, Model model) {

        // 查询统计结果： MySQL
        List<Calllog> logs =  callLogService.queryMonthDatas(tel, calltime);
        System.out.println(logs.size());
        model.addAttribute("calllogs", logs);
        return "view";
    }
}
