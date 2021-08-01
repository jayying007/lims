package edu.scnu.lims.controller;

import edu.scnu.lims.annotation.LoginRequired;
import edu.scnu.lims.common.CommonResult;
import edu.scnu.lims.constant.UserRoleEnum;
import edu.scnu.lims.constant.UserStatusEnum;
import edu.scnu.lims.entity.User;
import edu.scnu.lims.service.UserService;
import edu.scnu.lims.util.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author 庄杰颖
 */
@Api(tags = "用户管理")
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value = "注册账号")
    @PostMapping("register")
    public CommonResult<String> registerAccount(@RequestBody @Valid RegisterInfo registerInfo) {
        log.debug("注册信息:" + registerInfo);

        String verifyCode = (String)redisTemplate.opsForValue().get(registerInfo.getEmail());

        if(verifyCode == null) {
            return CommonResult.validateFailed("验证码不存在或已失效");
        }
        if(!verifyCode.equals(registerInfo.getVerifyCode())) {
            return CommonResult.validateFailed("验证码错误");
        }

        User user = new User();
        user.setEmail(registerInfo.getEmail());
        user.setName(registerInfo.getName());
        user.setPassword(DigestUtils.md5DigestAsHex(registerInfo.getPassword().getBytes()));
        user.setAvatar("https://iknow-pic.cdn.bcebos.com/0eb30f2442a7d933b9baa918ad4bd11373f0014e");
        user.setRole(UserRoleEnum.NORMAL);
        user.setStatus(UserStatusEnum.ENABLE);

        User res = userService.saveUser(user);
        if(res == null) {
            return CommonResult.failed("该邮箱已存在");
        }

        return CommonResult.success("注册成功");
    }

    @LoginRequired
    @ApiOperation(value = "用户下线")
    @PostMapping("logout")
    public CommonResult<String> logout(@RequestHeader(value = "Authentication-Token") String token) {
        //从redis移除user信息
        redisTemplate.delete(token);

        return CommonResult.success(null);
    }

    @LoginRequired
    @ApiOperation(value = "获取账号信息")
    @GetMapping("info")
    public CommonResult<User> getInfo(@RequestHeader(value = "Authentication-Token") String token) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);

        if(user == null) {
            return CommonResult.expire();
        }

        return CommonResult.success(user);
    }

    @LoginRequired
    @ApiOperation(value = "获取用户表")
    @GetMapping("list")
    public CommonResult<List<User>> getList(@RequestHeader(value = "Authentication-Token") String token,
                                            @RequestParam Integer page, @RequestParam Integer pageSize,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) String role,
                                            @RequestParam(required = false) String status) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }
        if(UserRoleEnum.SUPER_ADMIN == user.getRole()) {
            User user2 = new User();
            user2.setName(name);
            if(role != null) {
                user2.setRole(UserRoleEnum.valueOf(role));
            }
            if(status != null) {
                user2.setStatus(UserStatusEnum.valueOf(status));
            }

            return CommonResult.success(userService.listUsers(page, pageSize, user2));
        } else {
            return CommonResult.forbidden(null);
        }
    }

    @LoginRequired
    @ApiOperation(value = "获取用户数")
    @GetMapping("list/count")
    public CommonResult<Long> countList(@RequestHeader(value = "Authentication-Token") String token,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String role,
                                        @RequestParam(required = false) String status) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }
        if(UserRoleEnum.SUPER_ADMIN == user.getRole()) {
            User user2 = new User();
            user2.setName(name);
            if(role != null) {
                user2.setRole(UserRoleEnum.valueOf(role));
            }
            if(status != null) {
                user2.setStatus(UserStatusEnum.valueOf(status));
            }

            return CommonResult.success(userService.countUsers(user));
        } else {
            return CommonResult.forbidden(null);
        }
    }

    @LoginRequired
    @ApiOperation(value = "更新账号信息", notes = "传送需要更新的字段,但必须有ID")
    @PutMapping("info")
    public CommonResult<String> updateInfo(@RequestHeader(value = "Authentication-Token") String token,
                                         @RequestBody @Valid UserVO userVO) {
        log.info("计划更新的用户信息:" + userVO);
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }
        //初始化要更新的数据
        User user2 = new User();
        user2.setUserId(userVO.getUserId());
        user2.setName(userVO.getName());
        user2.setPassword(userVO.getPassword());
        user2.setRole(userVO.getRole());
        user2.setStatus(userVO.getStatus());
        user2.setAvatar(userVO.getAvatar());

        if(UserRoleEnum.SUPER_ADMIN == user.getRole() || Objects.equals(user.getUserId(), userVO.getUserId())) {
            //更新信息,同时存入redis --> 需要判断更新哪个(user,token)
            String updatedToken = (String)redisTemplate.opsForValue().get("userId-" + user2.getUserId());
            //为空说明该用户目前没有登录,redis找不到缓存
            if(updatedToken != null) {
                User tmp = userService.updateUser(user2, user.getRole());
                redisTemplate.opsForValue().set(updatedToken, GsonUtils.toJson(tmp), 1, TimeUnit.HOURS);
            } else {
                userService.updateUser(user2, user.getRole());
            }
            return CommonResult.success("用户信息更新成功");
        } else {
            //不是说明是别人乱改
            log.warn("用户id:" + user.getUserId() + " 试图修改用户id:" + userVO.getUserId());
            return CommonResult.forbidden(null);
        }
    }

    @Data
    static class RegisterInfo {
        @NotNull(message = "姓名不能为空")
        private String name;
        @NotNull(message = "密码不能为空")
        private String password;
        @Email(message = "邮箱格式错误")
        @NotNull(message = "邮箱不能为空")
        private String email;
        @NotNull(message = "验证码不能为空")
        private String verifyCode;
    }

    @Data
    static class UserVO {
        @NotNull(message = "用户ID不能为空")
        private Integer userId;

        private String name;

        private String password;

        private UserRoleEnum role;

        private String avatar;

        private UserStatusEnum status;
    }
}