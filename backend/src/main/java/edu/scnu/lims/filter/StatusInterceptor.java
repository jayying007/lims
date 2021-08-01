package edu.scnu.lims.filter;

import com.google.gson.Gson;
import edu.scnu.lims.annotation.LoginRequired;
import edu.scnu.lims.constant.UserStatusEnum;
import edu.scnu.lims.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 判断该用户是否能正常使用
 * @author ZhuangJieYing
 */
@Slf4j
public class StatusInterceptor implements HandlerInterceptor {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("请求路径:" + request.getRequestURI());
        log.info("请求方法:" + request.getMethod());

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 判断接口是否需要登录
        LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);

        if (methodAnnotation != null) {
            //这里从 http 请求头中取出 token
            String token = request.getHeader("Authentication-Token");
            if (token == null) {
                throw new RuntimeException("无token，请重新登录");
            }
            String userSerialize = (String)redisTemplate.opsForValue().get(token);
            Gson gson = new Gson();
            User user = gson.fromJson(userSerialize, User.class);
            if(user == null) {
                throw new RuntimeException("用户信息已过期");
            }
            if(UserStatusEnum.DISABLE == user.getStatus() && !request.getServletPath().equals("/user/logout")) {
                throw new RuntimeException("该用户目前处于禁用状态");
            }
        }
        return true;
    }
}
