package com.piems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.piems.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> getCategoriesByTypeAndUserId(Integer type, Long userId);
    Category addCategory(Category category);
    Category updateCategory(Category category);
    boolean deleteCategory(Long categoryId, Long userId);
}