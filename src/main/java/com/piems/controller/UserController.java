package com.piems.controller;

import com.piems.common.Result;
import com.piems.dto.UserDTO;
import com.piems.dto.LoginDTO;
import com.piems.entity.User;
import com.piems.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Api(tags = "用户管理")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<User> register(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.register(userDTO);
            return Result.success(user, "注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<User> login(@RequestBody LoginDTO loginDTO) {
        try {
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            return Result.success(user, "登录成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取用户信息")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新用户信息")
    public Result<User> updateUserInfo(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        User user = userService.updateUserInfo(id, userDTO);
        return Result.success(user, "更新成功");
    }

    @PutMapping("/{id}/password")
    @ApiOperation("修改密码")
    public Result<Boolean> changePassword(@PathVariable Long id, @RequestBody ChangePasswordDTO changePasswordDTO) {
        boolean success = userService.changePassword(id, changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());
        return success ? Result.success(true, "密码修改成功") : Result.error("密码修改失败");
    }

    // 内部静态类，用于接收请求参数
    static class ChangePasswordDTO {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}
