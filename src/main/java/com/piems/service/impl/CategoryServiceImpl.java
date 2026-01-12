package com.piems.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.piems.entity.Category;
import com.piems.mapper.CategoryMapper;
import com.piems.service.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getCategoriesByTypeAndUserId(Integer type, Long userId) {
        List<Category> allCategories = new ArrayList<>();
        
        // 获取系统默认分类
        QueryWrapper<Category> defaultWrapper = new QueryWrapper<>();
        defaultWrapper.eq("type", type)
                     .eq("user_id", 0L)
                     .eq("status", 1);
        List<Category> defaultCategories = baseMapper.selectList(defaultWrapper);
        
        // 获取用户自定义分类
        QueryWrapper<Category> userWrapper = new QueryWrapper<>();
        userWrapper.eq("type", type)
                  .eq("user_id", userId)
                  .eq("status", 1);
        List<Category> userCategories = baseMapper.selectList(userWrapper);
        
        allCategories.addAll(defaultCategories);
        allCategories.addAll(userCategories);
        return allCategories;
    }

    @Override
    public Category addCategory(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setStatus(1);
        baseMapper.insert(category);
        return category;
    }

    @Override
    public Category updateCategory(Category category) {
        Category existingCategory = baseMapper.selectById(category.getId());
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }
        
        category.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(category);
        return category;
    }

    @Override
    public boolean deleteCategory(Long categoryId, Long userId) {
        Category category = baseMapper.selectById(categoryId);
        if (category == null) {
            return false;
        }
        
        // 系统默认分类不能删除
        if (category.getUserId() == 0) {
            return false;
        }
        
        // 只能删除自己的分类
        if (!category.getUserId().equals(userId)) {
            return false;
        }
        
        baseMapper.deleteById(categoryId);
        return true;
    }
}
