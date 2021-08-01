package edu.scnu.lims.controller;

import edu.scnu.lims.common.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author ZhuangJieYing
 */
@Api(tags = "图片上传")
@Slf4j
@RestController
@RequestMapping("upload")
public class UploadController {
    private final String IMAGE_URL_PREFIX = "http://127.0.0.1:8080/image/";
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value = "图片上传")
    @PostMapping
    public CommonResult<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("上传文件名:" + file.getOriginalFilename());
        log.info("文件大小:" + file.getBytes().length);

        String fileName = System.currentTimeMillis() + "image.jpg";
        String filePath = System.getProperty("user.home") + "/lims/images/" + fileName;
        File newFile = new File(filePath);
        file.transferTo(newFile);

        String imageUrl = IMAGE_URL_PREFIX + fileName;

        return CommonResult.success(imageUrl);
    }
}
