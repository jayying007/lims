package edu.scnu.lims.controller;

import edu.scnu.lims.common.CommonResult;
import edu.scnu.lims.common.Jwt;
import edu.scnu.lims.entity.User;
import edu.scnu.lims.service.UserService;
import edu.scnu.lims.util.GsonUtils;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ZhuangJieYing
 */
@Slf4j
@Api(tags = "Token管理")
@RestController
@RequestMapping("token")
public class TokenController {
    private final Integer TOKEN_EXPIRE_TIME = 1000 * 60 * 60;
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 验证用户,生成token,并将用户信息存到redis中
     * @param loginInfo 登录信息(邮箱,密码)
     * @return token
     */
    @ApiOperation(value = "获取Token")
    @PostMapping
    public CommonResult<String> getToken(@RequestBody @Valid LoginInfo loginInfo) {
        String email = loginInfo.getEmail();
        String password = loginInfo.getPassword();
        //验证邮箱和密码是否一致
        User user = new User();
        user.setEmail(email);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));

        User user2 = userService.getUser(user);
        if(user2 == null) {
            log.info("账号或密码错误");
            return CommonResult.validateFailed("账号或密码错误");
        }
        Integer userId = user2.getUserId();
        //为其生成token
        String jwString = Jwts.builder()
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                .claim("id", userId)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRE_TIME))
                .signWith(Jwt.KEY)
                .compact();
        //将userId yu token 的关系写入redis
        redisTemplate.opsForValue().set("userId-" + userId, jwString, 1, TimeUnit.HOURS);
        //将token 与 user 的关系存入redis
        redisTemplate.opsForValue().set(jwString, GsonUtils.toJson(user2), 1, TimeUnit.HOURS);

        return CommonResult.success(jwString);
    }

    @Data
    static class LoginInfo {
        @Email(message = "邮箱格式错误")
        @NotNull(message = "邮箱不能为空")
        private String email;
        @NotNull(message = "密码不能为空")
        private String password;
    }
}
