package edu.scnu.lims.service;

import edu.scnu.lims.constant.UserRoleEnum;
import edu.scnu.lims.dao.UserDao;
import edu.scnu.lims.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author ZhuangJieYing
 */
@Slf4j
@Service
public class UserService {
    @Resource
    private UserDao userDao;

    /**
     * 新建用户,需检验注册的邮箱是否已存在
     * @param user 注册的用户信息
     * @return 若注册成功,返回该用户,否则返回null
     */
    public User saveUser(User user) {
        // 如果邮箱已存在,返回
        if(this.getUser(user) != null) {
            log.info("账号已存在");
            return null;
        }
        return userDao.save(user);
    }
    /**
     * 通过ID查找用户
     * @param userId 用户id
     * @return User或者Null
     */
    public User getUserById(Integer userId) {
        Optional<User> res = userDao.findById(userId);
        return res.orElse(null);
    }

    public User getUser(User user) {
        Example<User> example = Example.of(user);

        return userDao.findOne(example).orElse(null);
    }
    /**
     * 查找所有用户,需设置分页
     * @param page 第几页,从0开始
     * @param pageSize 一页的大小
     * @return User列表
     */
    public List<User> listUsers(int page, int pageSize, User user) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", GenericPropertyMatchers.contains());
        Example<User> example = Example.of(user, matcher);
        Page<User> users = userDao.findAll(example, PageRequest.of(page, pageSize));

        return users.toList();
    }
    public Long countUsers(User user) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", GenericPropertyMatchers.contains());
        Example<User> example = Example.of(user, matcher);

        return userDao.count(example);
    }
    /**
     * 如果user的属性为null,则会覆盖原来的数值
     * @param user 更新后的用户信息,未更新的属性默认为null
     * @return 返回更新后的用户信息
     */
    public User updateUser(User user, UserRoleEnum role) {
        User oldUser = this.getUserById(user.getUserId());
        if(oldUser != null) {
            User newUser = oldUser;
            //更新部分字段
            if(user.getName() != null) {
                newUser.setName(user.getName());
            }
            if(user.getPassword() != null) {
                newUser.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            }
            if(user.getAvatar() != null) {
                newUser.setAvatar(user.getAvatar());
            }
            //非超级管理员,即使是本人也无法修改这些字段
            if(UserRoleEnum.SUPER_ADMIN == role) {
                if(user.getRole() != null) {
                    newUser.setRole(user.getRole());
                }
                if(user.getStatus() != null) {
                    newUser.setStatus(user.getStatus());
                }
            }
            return userDao.save(newUser);
        }
        return null;
    }
}