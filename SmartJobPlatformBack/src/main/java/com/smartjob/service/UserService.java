package com.smartjob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartjob.common.Constants;
import com.smartjob.dto.*;
import com.smartjob.mapper.UserMapper;
import com.smartjob.model.User;
import com.smartjob.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public LoginResultVO login(LoginDTO dto, String role) {
        User user;
        if (Constants.ROLE_ADMIN.equals(role)) {
            user = this.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getRole, role)
                    .eq(User::getUsername, dto.getUsername()));
        } else {
            user = this.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getRole, role)
                    .eq(User::getPhone, dto.getPhone()));
        }
        
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        if (user.getStatus() != null && user.getStatus() == Constants.USER_STATUS_DISABLED) {
            throw new IllegalArgumentException("账号已被禁用");
        }
        
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }
        
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);
        
        String token = jwtUtil.generateToken(user.getId(), user.getRole(), user.getUsername());
        
        LoginResultVO result = new LoginResultVO();
        result.setToken(token);
        result.setUserInfo(convertToUserInfoVO(user));
        
        return result;
    }
    
    @Transactional
    public LoginResultVO register(RegisterDTO dto) {
        User existUser = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, dto.getPhone())
                .eq(User::getRole, Constants.ROLE_JOBSEEKER));
        
        if (existUser != null) {
            throw new IllegalArgumentException("手机号已被注册");
        }
        
        User user = new User();
        user.setUsername(dto.getName() != null ? dto.getName() : "user_" + System.currentTimeMillis());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Constants.ROLE_JOBSEEKER);
        user.setStatus(Constants.USER_STATUS_NORMAL);
        user.setAvatar(Constants.DEFAULT_AVATAR);
        user.setEmail(dto.getPhone() + "@example.com");
        this.save(user);
        
        String token = jwtUtil.generateToken(user.getId(), user.getRole(), user.getUsername());
        
        LoginResultVO result = new LoginResultVO();
        result.setToken(token);
        result.setUserInfo(convertToUserInfoVO(user));
        
        return result;
    }
    
    public UserInfoVO getCurrentUserInfo() {
        Long userId = getCurrentUserId();
        User user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return convertToUserInfoVO(user);
    }
    
    @Transactional
    public UserInfoVO updateProfile(User userDTO) {
        Long userId = getCurrentUserId();
        User user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        if (StringUtils.hasText(userDTO.getUsername())) {
            user.setUsername(userDTO.getUsername());
        }
        if (StringUtils.hasText(userDTO.getEmail())) {
            user.setEmail(userDTO.getEmail());
        }
        if (StringUtils.hasText(userDTO.getAvatar())) {
            user.setAvatar(userDTO.getAvatar());
        }
        if (userDTO.getGender() != null) {
            user.setGender(userDTO.getGender());
        }
        if (userDTO.getBirthday() != null) {
            user.setBirthday(userDTO.getBirthday());
        }
        if (StringUtils.hasText(userDTO.getCity())) {
            user.setCity(userDTO.getCity());
        }
        
        this.updateById(user);
        return convertToUserInfoVO(user);
    }
    
    @Transactional
    public void changePassword(ChangePasswordDTO dto) {
        Long userId = getCurrentUserId();
        User user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        this.updateById(user);
    }
    
    public Page<User> getUserPage(Integer page, Integer pageSize, String keyword, String role) {
        Page<User> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(User::getUsername, keyword)
                    .or().like(User::getPhone, keyword));
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        return this.page(pageParam, wrapper);
    }
    
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        User user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setStatus(status);
        this.updateById(user);
    }
    
    private Long getCurrentUserId() {
        try {
            Long userId = (Long) org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
            if (userId == null) {
                throw new IllegalArgumentException("未登录");
            }
            return userId;
        } catch (Exception e) {
            throw new IllegalArgumentException("未登录或token已过期");
        }
    }
    
    private UserInfoVO convertToUserInfoVO(User user) {
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        if (user.getCreateTime() != null) {
            vo.setCreateTime(user.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        return vo;
    }
    
    @Transactional
    public Map<String, String> uploadAvatar(String avatarUrl) {
        Long userId = getCurrentUserId();
        User user = this.getById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        user.setAvatar(avatarUrl);
        this.updateById(user);
        
        Map<String, String> result = new HashMap<>();
        result.put("url", avatarUrl);
        return result;
    }
}
