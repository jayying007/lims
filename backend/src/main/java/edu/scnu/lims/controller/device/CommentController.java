package edu.scnu.lims.controller.device;

import edu.scnu.lims.annotation.LoginRequired;
import edu.scnu.lims.common.CommonResult;
import edu.scnu.lims.entity.Device;
import edu.scnu.lims.entity.DeviceComment;
import edu.scnu.lims.entity.User;
import edu.scnu.lims.service.DeviceCommentService;
import edu.scnu.lims.util.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author ZhuangJieYing
 */
@Slf4j
@Api(tags = "设备评论")
@RestController
@RequestMapping("device")
public class CommentController {
    @Resource
    private DeviceCommentService deviceCommentService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @LoginRequired
    @ApiOperation(value = "添加设备评论")
    @PostMapping("comment")
    public CommonResult<DeviceComment> addComment(@RequestHeader(value = "Authentication-Token") String token,
                                                  @RequestBody @Valid DeviceCommentVO deviceCommentVO) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }

        DeviceComment deviceComment = new DeviceComment();
        deviceComment.setComment(deviceCommentVO.getComment());
        deviceComment.setDevice(deviceCommentVO.getDevice());
        deviceComment.setUser(user);
        deviceComment.setCommentTimestamp(System.currentTimeMillis());

        return CommonResult.success(deviceCommentService.saveComment(deviceComment));
    }

    @LoginRequired
    @ApiOperation(value = "查看设备评论")
    @GetMapping("comment")
    public CommonResult<List<DeviceComment>> getComment(@RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "50") Integer pageSize,
                                                        @RequestParam Integer deviceId) {
        DeviceComment deviceComment = new DeviceComment();
        Device device = new Device();
        device.setDeviceId(deviceId);
        deviceComment.setDevice(device);

        return CommonResult.success(deviceCommentService.getComments(page, pageSize, deviceComment));
    }

    @Data
    static class DeviceCommentVO {
        private String comment;
        private Device device;
    }
}
