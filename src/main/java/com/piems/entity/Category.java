package com.piems.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer type; // 1-收入，2-支出
    private String icon; // 分类图标（可选）
    private Integer userId; // 0表示系统默认分类
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;

}