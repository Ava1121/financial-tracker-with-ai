package com.piems.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.piems.entity.Record;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class ExcelUtil {

    /**
     * 导出收支记录到Excel
     * @param records 收支记录列表
     * @param fileName 文件名
     */
    public static void exportRecordsToExcel(List<Record> records, String fileName) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("获取请求上下文失败");
        }
        
        HttpServletResponse response = attributes.getResponse();
        if (response == null) {
            throw new RuntimeException("获取响应对象失败");
        }
        
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 对文件名进行URLEncoder编码，防止中文乱码
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
            
            // 使用EasyExcel进行导出
            EasyExcel.write(response.getOutputStream(), Record.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 自动调整列宽
                    .sheet("收支记录")
                    .doWrite(records);
        } catch (IOException e) {
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }
}
