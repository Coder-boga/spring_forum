package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.UserMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Article;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectByUserName(String username) {
        if (!StringUtils.hasLength(username)) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        return userMapper.selectByUserName(username);
    }

    @Override
    public void create(User user) {
        if (user == null || !StringUtils.hasLength(user.getUsername())
                || !StringUtils.hasLength(user.getNickname()) || !StringUtils.hasLength(user.getPassword())
                || !StringUtils.hasLength(user.getSalt())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        User existUser = userMapper.selectByUserName(user.getUsername());
        if (existUser != null) {
            log.warn(ResultCode.FAILED_USER_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
        }

        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);

        int row = userMapper.insertSelective(user);
        if (row != 1) {
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        log.info("新增用户成功，username = " + user.getUsername());
    }

    @Override
    public User login(String username, String password) {
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        User user = userMapper.selectByUserName(username);
        if (user == null) {
            log.warn(ResultCode.FAILED_LOGIN.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        String encryptPassword = MD5Util.md5Salt(password, user.getSalt());
        if (!user.getPassword().equals(encryptPassword)) {
            log.warn(ResultCode.FAILED_LOGIN.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }

        log.info("登陆成功, username = " + username);
        return user;
    }

    @Override
    public User selectById(Long id) {
        if (id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        User user = userMapper.selectByPrimaryKey(id);
        return user;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        if (id == null || id < 0) {
            log.warn(ResultCode.FAILED_USER_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_ARTICLE_COUNT));
        }
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount() + 1);
        Date date = new Date();
        updateUser.setCreateTime(date);
        updateUser.setUpdateTime(date);
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
    }

    @Override
    public void subOneArticleCountById(Long id) {
        if (id == null || id < 0) {
            log.warn(ResultCode.FAILED_USER_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_ARTICLE_COUNT));
        }
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null || user.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        if (user.getState() == 1) {
            log.warn(ResultCode.FAILED_USER_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_BANNED));
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount() - 1);
        updateUser.setUpdateTime(new Date());
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void modifyInfo(User user) {
        if (user == null || user.getId() == null || user.getId() <= 0) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        User existsUser = userMapper.selectByPrimaryKey(user.getId());
        if (existsUser == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        boolean checkAttr = false;

        User updateUser = new User();
        updateUser.setId(user.getId());

        if (StringUtils.hasLength(user.getNickname()) && !user.getNickname().equals(existsUser.getNickname())) {
            updateUser.setNickname(user.getNickname());
            checkAttr = true;
        }

        if (user.getGender() != null && user.getGender() != existsUser.getGender()) {
            updateUser.setGender(user.getGender());
            if (updateUser.getGender() > 2 || updateUser.getGender() < 0) {
                updateUser.setGender((byte) 2);
            }
            checkAttr = true;
        }

        if (StringUtils.hasLength(user.getEmail()) && !user.getEmail().equals(existsUser.getEmail())) {
            updateUser.setEmail(user.getEmail());
            checkAttr = true;
        }

        if (StringUtils.hasLength(user.getPhoneNum()) && !user.getPhoneNum().equals(existsUser.getPhoneNum())) {
            updateUser.setPhoneNum(user.getPhoneNum());
            checkAttr = true;
        }

        if (StringUtils.hasLength(user.getRemark()) && !user.getRemark().equals(existsUser.getRemark())) {
            updateUser.setRemark(user.getRemark());
            checkAttr = true;
        }

        if (checkAttr == false) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void modifyPassword(Long id, String newPssword, String oldPassword) {
        if (id == null || id <= 0 || !StringUtils.hasLength(newPssword) || !StringUtils.hasLength(oldPassword)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        User user = userMapper.selectByPrimaryKey(id);
        if (user == null || user.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        String oldEncryptPassword = MD5Util.md5Salt(oldPassword, user.getSalt());
        if (!oldEncryptPassword.equalsIgnoreCase(user.getPassword())) {
            log.warn(ResultCode.FAILED_PASSWORD_NOT_SAME.getMessage());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PASSWORD_NOT_SAME));
        }

        String salt = UUIDUtil.UUID_32();
        String encryptPassword = MD5Util.md5Salt(newPssword, salt);
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setSalt(salt);
        updateUser.setPassword(encryptPassword);
        updateUser.setUpdateTime(new Date());

        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if (row != 1) {
            log.warn(ResultCode.FAILED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }


}
