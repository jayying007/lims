package edu.scnu.lims.config;

import edu.scnu.lims.dao.ScheduleTaskMapper;
import edu.scnu.lims.entity.RemindObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jay
 * @since 2021/4/8
 */
@Configuration
@EnableScheduling
@Slf4j
public class ScheduleTask {
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
    ScheduleTaskMapper scheduleTaskMapper;

    @Scheduled(cron = "0 0 0 1/3 * ?")
    public void remindRecord() {
        List<RemindObject> list = scheduleTaskMapper.getRemindRecord();
        for(RemindObject object : list) {
            //发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(object.getEmail());//收信人
            message.setSubject("实验室设备管理平台 - 提醒");//主题
            message.setText(String.format("你借用的设备【%s】近三天未上报设备情况，请及时处理。", object.getDeviceName()));//内容
            message.setFrom(from);//发信人
            mailSender.send(message);
        }
    }

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void remindReturn() {
        List<RemindObject> list = scheduleTaskMapper.getRemindReturn();
        for(RemindObject object : list) {
            //发送邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(object.getEmail());//收信人
            message.setSubject("实验室设备管理平台 - 提醒");//主题
            message.setText(String.format("你借用的设备【%s】已经逾期，请归还设备。", object.getDeviceName()));//内容
            message.setFrom(from);//发信人
            mailSender.send(message);
        }
    }
}