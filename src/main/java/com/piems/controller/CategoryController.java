package com.piems.controller;

import com.piems.common.Result;
import com.piems.entity.Category;
import com.piems.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@Api(tags = "分类管理")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    @ApiOperation("获取分类列表")
    public Result<List<Category>> getCategories(@RequestParam Integer type, @RequestParam Long userId) {
        List<Category> categories = categoryService.getCategoriesByTypeAndUserId(type, userId);
        return Result.success(categories);
    }

    @PostMapping
    @ApiOperation("添加分类")
    public Result<Category> addCategory(@RequestBody Category category) {
        try {
            Category newCategory = categoryService.addCategory(category);
            return Result.success(newCategory, "添加成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("更新分类")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        try {
            category.setId(id);
            Category updatedCategory = categoryService.updateCategory(category);
            return Result.success(updatedCategory, "更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除分类")
    public Result<Boolean> deleteCategory(@PathVariable Long id, @RequestParam Long userId) {
        boolean success = categoryService.deleteCategory(id, userId);
        return success ? Result.success(true, "删除成功") : Result.error("删除失败");
    }
}
