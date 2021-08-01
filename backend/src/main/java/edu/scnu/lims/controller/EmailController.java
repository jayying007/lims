package edu.scnu.lims.controller;

import edu.scnu.lims.common.CommonResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author ZhuangJieYing
 */
@Slf4j
@RestController
@RequestMapping("email")
public class EmailController {
    /**
     * 使用@Value注入application.properties中指定的用户名
     */
    @Value("${spring.mail.username}")
    private String from;
    /**
     * 用于发送邮件
     */
    @Resource
    private JavaMailSender mailSender;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 验证码有效期
     */
    private final Integer VERIFY_CODE_EXPIRE_TIME = 60 * 5;
    /**
     * 邮件发送线程池
     */
    private final ThreadFactory springThreadFactory = new CustomizableThreadFactory("springThread-pool-email");
    private final ExecutorService executor = new ThreadPoolExecutor(4, 8,
            5, TimeUnit.MINUTES, new ArrayBlockingQueue<>(50), springThreadFactory);

    @ApiOperation(value = "获取验证码")
    @GetMapping("verifyCode")
    public CommonResult<String> sendVerifyCode(@RequestParam String email) {
        //生成六位数的验证码
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        log.info("生成验证码:" + verifyCode);
        //将验证码存到Redis
        redisTemplate.opsForValue().set(email, verifyCode, VERIFY_CODE_EXPIRE_TIME, TimeUnit.SECONDS);

        executor.execute(new EmailThread(email, verifyCode));

        return CommonResult.success("已将验证码发送至邮箱");
    }

    class EmailThread implements Runnable {
        private final String receiver;
        private final String verifyCode;

        public EmailThread(String receiver, String verifyCode) {
            this.receiver = receiver;
            this.verifyCode = verifyCode;
        }

        @Override
        public void run() {
            //发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(receiver);//收信人
            message.setSubject("实验室设备管理平台 - 验证码");//主题
            message.setText("这是你的验证码:" + verifyCode + ",5分钟内有效");//内容
            message.setFrom(from);//发信人
            mailSender.send(message);
        }
    }
}
