package com.atguigu.ct.analysis;

import com.atguigu.ct.analysis.tool.AnalysisBeanTool;
import com.atguigu.ct.analysis.tool.AnalysisTextTool;
import org.apache.hadoop.util.ToolRunner;

/**
 * @author springdu
 * @create 2020/12/29 9:22
 * @description TODO 分析数据
 */
public class AnalysisData {
    public static void main(String[] args) throws Exception{

//        int result = ToolRunner.run(new AnalysisTextTool(), args);
        int result = ToolRunner.run(new AnalysisBeanTool(), args);


    }
}
