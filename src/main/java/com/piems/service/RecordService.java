package com.piems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.piems.entity.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RecordService extends IService<Record> {
    Record addRecord(Record record);
    Record updateRecord(Record record);
    boolean deleteRecord(Long recordId, Long userId);
    List<Record> getRecordsByCondition(Long userId, Integer type, Long categoryId, LocalDateTime startTime, LocalDateTime endTime);
    
    Map<String, Object> getMonthlySummary(Long userId, Integer year, Integer month);
    List<Map<String, Object>> getTopCategories(Long userId, Integer type, Integer limit, LocalDateTime startTime, LocalDateTime endTime);
    Map<String, Object> getAnnualSummary(Long userId, Integer year);
    
    Map<String, Object> getAIFinancialAnalysis(Long userId, String question);
}