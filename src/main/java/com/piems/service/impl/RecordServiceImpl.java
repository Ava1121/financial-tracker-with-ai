package com.piems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piems.entity.Category;
import com.piems.entity.Record;
import com.piems.mapper.CategoryMapper;
import com.piems.mapper.RecordMapper;
import com.piems.service.RecordService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

    private final CategoryMapper categoryMapper;

    public RecordServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Record addRecord(Record record) {
        // 获取分类名称
        Category category = categoryMapper.selectById(record.getCategoryId());
        if (category != null) {
            record.setCategoryName(category.getName());
        }
        
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setStatus(1);
        baseMapper.insert(record);
        return record;
    }

    @Override
    public Record updateRecord(Record record) {
        Record existingRecord = baseMapper.selectById(record.getId());
        if (existingRecord == null) {
            throw new RuntimeException("记录不存在");
        }
        
        // 获取分类名称
        Category category = categoryMapper.selectById(record.getCategoryId());
        if (category != null) {
            record.setCategoryName(category.getName());
        }
        
        record.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(record);
        return record;
    }

    @Override
    public boolean deleteRecord(Long recordId, Long userId) {
        Record record = baseMapper.selectById(recordId);
        if (record == null) {
            return false;
        }
        
        // 只能删除自己的记录
        if (!record.getUserId().equals(userId)) {
            return false;
        }
        
        baseMapper.deleteById(recordId);
        return true;
    }

    @Override
    public List<Record> getRecordsByCondition(Long userId, Integer type, Long categoryId, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        if (type != null) {
            queryWrapper.eq("type", type);
        }
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        // 只处理非null的时间参数
        if (startTime != null) {
            queryWrapper.ge("record_time", startTime);
        }
        
        if (endTime != null) {
            queryWrapper.le("record_time", endTime);
        }
        
        queryWrapper.orderByDesc("record_time");
        
        List<Record> records = baseMapper.selectList(queryWrapper);
        System.out.println("查询记录数: " + records.size());
        return records;
    }

    @Override
    public Map<String, Object> getMonthlySummary(Long userId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        
        // 获取当月收支记录
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .ge("record_time", startTime)
                   .le("record_time", endTime);
        
        List<Record> records = baseMapper.selectList(queryWrapper);
        
        // 计算总收入、总支出、结余
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;
        
        for (Record record : records) {
            if (record.getType() == 1) {
                income = income.add(record.getAmount());
            } else if (record.getType() == 2) {
                expense = expense.add(record.getAmount());
            }
        }
        
        BigDecimal balance = income.subtract(expense);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("income", income);
        summary.put("expense", expense);
        summary.put("balance", balance);
        
        return summary;
    }

    @Override
    public List<Map<String, Object>> getTopCategories(Long userId, Integer type, Integer limit, LocalDateTime startTime, LocalDateTime endTime) {
        // For now, return empty list until we implement this with QueryWrapper or MyBatis-Plus LambdaQuery
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> getAnnualSummary(Long userId, Integer year) {
        LocalDateTime startTime = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        
        // 获取全年收支记录
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .ge("record_time", startTime)
                   .le("record_time", endTime);
        
        List<Record> records = baseMapper.selectList(queryWrapper);
        
        // 按月份统计收支
        List<BigDecimal> monthlyIncome = new ArrayList<>(12);
        List<BigDecimal> monthlyExpense = new ArrayList<>(12);
        
        for (int i = 0; i < 12; i++) {
            monthlyIncome.add(BigDecimal.ZERO);
            monthlyExpense.add(BigDecimal.ZERO);
        }
        
        for (Record record : records) {
            int month = record.getRecordTime().getMonthValue() - 1;
            if (record.getType() == 1) {
                monthlyIncome.set(month, monthlyIncome.get(month).add(record.getAmount()));
            } else if (record.getType() == 2) {
                monthlyExpense.set(month, monthlyExpense.get(month).add(record.getAmount()));
            }
        }
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("monthlyIncome", monthlyIncome);
        summary.put("monthlyExpense", monthlyExpense);
        
        return summary;
    }

    @Override
    public Map<String, Object> getAIFinancialAnalysis(Long userId, String question) {
        // 获取用户最近3个月的收支记录
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMonths(3);
        
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .ge("record_time", startTime)
                   .le("record_time", endTime)
                   .orderByDesc("record_time");
        
        List<Record> records = baseMapper.selectList(queryWrapper);
        
        // 计算关键财务指标
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryExpenses = new HashMap<>();
        
        for (Record record : records) {
            if (record.getType() == 1) {
                totalIncome = totalIncome.add(record.getAmount());
            } else if (record.getType() == 2) {
                totalExpense = totalExpense.add(record.getAmount());
                // 按分类统计支出
                categoryExpenses.put(record.getCategoryName(), 
                    categoryExpenses.getOrDefault(record.getCategoryName(), BigDecimal.ZERO)
                    .add(record.getAmount()));
            }
        }
        
        BigDecimal savings = totalIncome.subtract(totalExpense);
        double savingsRate = totalIncome.compareTo(BigDecimal.ZERO) == 0 ? 0 : 
            savings.divide(totalIncome, 2, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
        
        // 准备数据用于调用外部AI API
        Map<String, Object> aiAnalysis = new HashMap<>();
        aiAnalysis.put("totalIncome", totalIncome);
        aiAnalysis.put("totalExpense", totalExpense);
        aiAnalysis.put("savings", savings);
        aiAnalysis.put("savingsRate", savingsRate);
        aiAnalysis.put("categoryExpenses", categoryExpenses);
        
        // 调用本地部署的DeepSeek API
        String aiResponse = "";
        try {
            // 1. 准备财务分析提示词
            StringBuilder prompt = new StringBuilder();
            prompt.append("你是一位专业的AI理财助手，请根据以下财务数据，回答用户的问题：\n\n");
            prompt.append("### 财务数据\n");
            prompt.append("1. 最近3个月总收入：¥").append(totalIncome).append("\n");
            prompt.append("2. 最近3个月总支出：¥").append(totalExpense).append("\n");
            prompt.append("3. 最近3个月结余：¥").append(savings).append("\n");
            prompt.append("4. 储蓄率：").append(String.format("%.1f", savingsRate)).append("%\n");
            prompt.append("5. 主要支出类别：\n");
            categoryExpenses.forEach((category, amount) -> {
                prompt.append("   - ").append(category).append("：¥").append(amount).append("\n");
            });
            prompt.append("\n### 用户问题\n");
            prompt.append(question != null && !question.isEmpty() ? question : "请提供个性化的理财建议");
            prompt.append("\n\n请使用简洁明了的语言，避免专业术语，给出具体、实用的回答。");
            
            // 2. 构建Ollama API请求
            RestTemplate restTemplate = new RestTemplate();
            
            // 配置Ollama API地址（默认端口为11434）
            String ollamaApiUrl = "http://localhost:11434/api/chat"; // Ollama默认API地址
            
            // 3. 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Ollama本地部署通常不需要API Key认证
            
            // 4. 构建请求体（Ollama API格式）
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-r1"); // 使用Ollama中的deepseek-r1模型
            requestBody.put("temperature", 0.7); // 控制输出随机性
            requestBody.put("stream", false); // 禁用流式输出，便于处理响应
            
            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt.toString());
            messages.add(userMessage);
            requestBody.put("messages", messages);
            
            System.out.println("准备调用Ollama API，模型: deepseek-r1");
            System.out.println("请求prompt: " + prompt.toString());
            
            // 5. 发送请求
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(ollamaApiUrl, requestEntity, Map.class);
            
            System.out.println("Ollama API响应状态码: " + response.getStatusCode());
            System.out.println("Ollama API响应体: " + response.getBody());
            
            // 6. 处理响应（Ollama API格式）
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, String> message = (Map<String, String>) responseBody.get("message");
                if (message != null) {
                    aiResponse = message.get("content");
                    System.out.println("AI响应内容: " + aiResponse);
                } else {
                    System.out.println("AI响应message为null");
                }
            } else {
                System.out.println("Ollama API调用失败，状态码: " + response.getStatusCode());
            }
            
            // 如果DeepSeek返回的响应为空，使用默认回答
            if (aiResponse == null || aiResponse.isEmpty()) {
                System.out.println("AI响应为空，使用默认回答");
                aiResponse = "根据您最近3个月的消费习惯，建议您适当减少" + 
                    categoryExpenses.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse("") + "类别的支出。" +
                    "您的储蓄率为" + String.format("%.1f", savingsRate) + "%，建议目标设定为30%以上。" +
                    "建议您建立紧急储备金，金额约为3-6个月的生活费。" +
                    "可以考虑将部分储蓄用于低风险投资，如货币基金或债券。";
            }
        } catch (Exception e) {
            // 处理API调用异常
            System.err.println("调用本地DeepSeek API失败: " + e.getMessage());
            e.printStackTrace();
            // 失败时使用默认回答
            aiResponse = "很抱歉，暂时无法为您提供AI分析，请稍后再试。\n" +
                        "根据您的财务数据，建议您适当控制支出，提高储蓄率。";
        }
        
        aiAnalysis.put("response", aiResponse);
        aiAnalysis.put("analysisTime", LocalDateTime.now());
        aiAnalysis.put("aiProvider", "本地部署 DeepSeek");
        
        return aiAnalysis;
    }
}
