package com.piems.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.piems.dto.UserDTO;
import com.piems.entity.User;

public interface UserService extends IService<User> {
    User register(UserDTO userDTO);
    User login(String username, String password);
    User updateUserInfo(Long userId, UserDTO userDTO);
    User getUserById(Long userId);
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}