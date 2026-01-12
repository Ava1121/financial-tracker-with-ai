package com.piems.controller;

import com.piems.common.Result;
import com.piems.entity.Record;
import com.piems.service.RecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/record")
@Api(tags = "收支记录管理")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    @ApiOperation("添加收支记录")
    public Result<Record> addRecord(@RequestBody Record record) {
        try {
            Record newRecord = recordService.addRecord(record);
            return Result.success(newRecord, "添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("更新收支记录")
    public Result<Record> updateRecord(@PathVariable Long id, @RequestBody Record record) {
        try {
            record.setId(id);
            Record updatedRecord = recordService.updateRecord(record);
            return Result.success(updatedRecord, "更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除收支记录")
    public Result<Boolean> deleteRecord(@PathVariable Long id, @RequestParam Long userId) {
        boolean success = recordService.deleteRecord(id, userId);
        return success ? Result.success(true, "删除成功") : Result.error("删除失败");
    }

    @GetMapping("/list")
    @ApiOperation("获取收支记录列表")
    public Result<List<Record>> getRecords(@RequestParam Long userId,
                                          @RequestParam(required = false) Integer type,
                                          @RequestParam(required = false) Long categoryId,
                                          @RequestParam(required = false) LocalDateTime startTime,
                                          @RequestParam(required = false) LocalDateTime endTime) {
        List<Record> records = recordService.getRecordsByCondition(userId, type, categoryId, startTime, endTime);
        return Result.success(records);
    }

    @GetMapping("/monthly-summary")
    @ApiOperation("获取月度收支汇总")
    public Result<Map<String, Object>> getMonthlySummary(@RequestParam Long userId,
                                                       @RequestParam Integer year,
                                                       @RequestParam Integer month) {
        Map<String, Object> summary = recordService.getMonthlySummary(userId, year, month);
        return Result.success(summary);
    }

    @GetMapping("/top-categories")
    @ApiOperation("获取TOP分类")
    public Result<List<Map<String, Object>>> getTopCategories(@RequestParam Long userId,
                                                            @RequestParam Integer type,
                                                            @RequestParam(defaultValue = "3") Integer limit,
                                                            @RequestParam(required = false) LocalDateTime startTime,
                                                            @RequestParam(required = false) LocalDateTime endTime) {
        List<Map<String, Object>> topCategories = recordService.getTopCategories(userId, type, limit, startTime, endTime);
        return Result.success(topCategories);
    }

    @GetMapping("/annual-summary")
    @ApiOperation("获取年度收支汇总")
    public Result<Map<String, Object>> getAnnualSummary(@RequestParam Long userId,
                                                      @RequestParam Integer year) {
        Map<String, Object> summary = recordService.getAnnualSummary(userId, year);
        return Result.success(summary);
    }
    
    @GetMapping("/ai-analysis")
    @ApiOperation("获取AI财务分析")
    public ResponseEntity<Result<Map<String, Object>>> getAIAnalysis(@RequestParam Long userId, 
                                                                   @RequestParam(required = false) String question) {
        Map<String, Object> analysis = recordService.getAIFinancialAnalysis(userId, question);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return new ResponseEntity<>(Result.success(analysis), headers, HttpStatus.OK);
    }
}
