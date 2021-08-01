package edu.scnu.lims.controller.device;

import edu.scnu.lims.annotation.LoginRequired;
import edu.scnu.lims.common.CommonResult;
import edu.scnu.lims.entity.Device;
import edu.scnu.lims.entity.DeviceRecord;
import edu.scnu.lims.entity.User;
import edu.scnu.lims.service.DeviceRecordService;
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
@Api(tags = "设备记录")
@RestController
@RequestMapping("device")
public class RecordController {
    @Resource
    private DeviceRecordService deviceRecordService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @LoginRequired
    @ApiOperation(value = "上报设备使用情况")
    @PostMapping("record")
    public CommonResult<DeviceRecord> addRecord(@RequestHeader(value = "Authentication-Token") String token,
                                                @RequestBody @Valid DeviceRecordVO deviceRecordVO) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }

        DeviceRecord deviceRecord = new DeviceRecord();
        deviceRecord.setUser(user);
        deviceRecord.setRecordImageUrl(deviceRecordVO.getRecordImageUrl());
        deviceRecord.setDescription(deviceRecordVO.getDescription());
        deviceRecord.setDevice(deviceRecordVO.getDevice());
        deviceRecord.setRecordTimestamp(System.currentTimeMillis());

        return CommonResult.success(deviceRecordService.saveRecord(deviceRecord));
    }

    @LoginRequired
    @ApiOperation(value = "查看设备使用记录")
    @GetMapping("record")
    public CommonResult<List<DeviceRecord>> getRecord(@RequestParam(defaultValue = "0") Integer page,
                                                      @RequestParam(defaultValue = "50") Integer pageSize,
                                                      @RequestParam Integer deviceId) {
        DeviceRecord deviceRecord = new DeviceRecord();
        Device device = new Device();
        device.setDeviceId(deviceId);
        deviceRecord.setDevice(device);

        return CommonResult.success(deviceRecordService.getRecords(page, pageSize, deviceRecord));
    }

    @Data
    static class DeviceRecordVO {
        private String recordImageUrl;
        private String description;
        private Device device;
    }
}
