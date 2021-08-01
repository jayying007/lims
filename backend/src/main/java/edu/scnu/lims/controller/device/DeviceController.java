package edu.scnu.lims.controller.device;

import edu.scnu.lims.annotation.LoginRequired;
import edu.scnu.lims.common.CommonResult;
import edu.scnu.lims.constant.DeviceStatusEnum;
import edu.scnu.lims.constant.UserRoleEnum;
import edu.scnu.lims.dao.DeviceDao;
import edu.scnu.lims.dao.TopDeviceMapper;
import edu.scnu.lims.entity.*;
import edu.scnu.lims.service.*;
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
@Api(tags = "设备管理")
@RestController
@RequestMapping("device")
public class DeviceController {
    @Resource
    private DeviceService deviceService;
    @Resource
    private TopDeviceMapper topDeviceMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @LoginRequired
    @ApiOperation(value = "添加设备", notes = "需要ADMIN及以上级别")
    @PostMapping
    public CommonResult<Device> addDevice(@RequestHeader(value = "Authentication-Token") String token,
                                          @RequestBody @Valid DeviceVO deviceVO) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }
        if(UserRoleEnum.NORMAL == user.getRole()) {
            return CommonResult.forbidden(null);
        } else {
            Device device = new Device();
            device.setName(deviceVO.getName());
            device.setStatus(DeviceStatusEnum.AVAILABLE);
            device.setImageUrl(deviceVO.getImageUrl());
            device.setLaboratory(deviceVO.getLaboratory());
            device.setPropertyList(deviceVO.getPropertyList());
            device.setAppendixList(deviceVO.getAppendixList());

            return CommonResult.success(deviceService.saveDevice(device));
        }
    }

    @LoginRequired
    @ApiOperation(value = "查询某件设备")
    @GetMapping
    public CommonResult<Device> getDevice(@RequestParam Integer deviceId) {
        Device device = new Device();
        device.setDeviceId(deviceId);

        Device device2 = deviceService.getDevice(device);
        if(device2 == null) {
            CommonResult.failed("设备不存在");
        }
        return CommonResult.success(device2);
    }

    @LoginRequired
    @ApiOperation(value = "查询设备列表")
    @GetMapping("list")
    public CommonResult<List<Device>> getDeviceList(@RequestParam Integer page, @RequestParam Integer pageSize,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) DeviceStatusEnum status) {
        Device device = new Device();
        device.setName(name);
        device.setStatus(status);

        return CommonResult.success(deviceService.getDevices(page, pageSize, device));
    }

    @LoginRequired
    @ApiOperation(value = "统计设备数量")
    @GetMapping("list/count")
    public CommonResult<Long> countDeviceList(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) DeviceStatusEnum status) {
        Device device = new Device();
        device.setName(name);
        device.setStatus(status);

        return CommonResult.success(deviceService.countDevices(device));
    }

    @LoginRequired
    @ApiOperation(value = "更新设备信息", notes = "需要ADMIN及以上级别,只能更新自己创建的,SUPER_ADMIN可删除其他设备")
    @PutMapping
    public CommonResult<Device> updateDevice(@RequestHeader(value = "Authentication-Token") String token,
                                             @RequestBody @Valid DeviceVO deviceVO) {
        //已借出的设备无法更新状态
        if(deviceService.getDeviceById(deviceVO.getDeviceId()).getStatus() == DeviceStatusEnum.BORROWED
        && !deviceVO.status.equals("BORROWED")) {
            return CommonResult.failed("该设备已借出，无法更新状态");
        }
        //非本人无法更新
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }
        Device oldDevice = deviceService.getDeviceById(deviceVO.getDeviceId());
        if(!oldDevice.getLaboratory().getUser().getUserId().equals(user.getUserId())) {
            return CommonResult.forbidden(null);
        }

        Device device = new Device();
        device.setDeviceId(deviceVO.getDeviceId());
        device.setName(deviceVO.getName());
        switch (deviceVO.status) {
            case "AVAILABLE":
                device.setStatus(DeviceStatusEnum.AVAILABLE);
                break;
            case "BORROWED":
                device.setStatus(DeviceStatusEnum.BORROWED);
                break;
            case "REPAIRING":
                device.setStatus(DeviceStatusEnum.REPAIRING);
                break;
            case "DAMAGED":
                device.setStatus(DeviceStatusEnum.DAMAGED);
                break;
        }
        device.setImageUrl(deviceVO.getImageUrl());
        device.setLaboratory(deviceVO.getLaboratory());
        // 更新属性和附件时,需要提供对应的设备id; 不然默认新增,并且清除原有属性和附件,即使你带了属性或附件id
        for(int i = 0; i < deviceVO.getPropertyList().size(); i++) {
            Device device2 = new Device();
            device2.setDeviceId(deviceVO.getDeviceId());
            deviceVO.getPropertyList().get(i).setDevice(device2);
        }
        for(int i = 0; i < deviceVO.getAppendixList().size(); i++) {
            Device device2 = new Device();
            device2.setDeviceId(deviceVO.getDeviceId());
            deviceVO.getAppendixList().get(i).setDevice(device2);
        }
        device.setPropertyList(deviceVO.getPropertyList());
        device.setAppendixList(deviceVO.getAppendixList());

        return CommonResult.success(deviceService.updateDevice(device));
    }

    @LoginRequired
    @ApiOperation(value = "删除设备", notes = "需要ADMIN及以上级别,ADMIN只能删除自己创建的")
    @DeleteMapping
    public CommonResult<String> deleteDevice(@RequestHeader(value = "Authentication-Token") String token,
                                             @RequestParam Integer deviceId) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }

        if(UserRoleEnum.NORMAL == user.getRole()) {
            return CommonResult.forbidden(null);
        } else if(UserRoleEnum.ADMIN == user.getRole()) {
            Device oldDevice = deviceService.getDeviceById(deviceId);
            if(oldDevice != null && oldDevice.getLaboratory().getUser().getUserId().equals(user.getUserId())) {
                deviceService.deleteDeviceById(deviceId);

                return CommonResult.success("操作成功");
            }
            return CommonResult.forbidden(null);
        } else {
            deviceService.deleteDeviceById(deviceId);

            return CommonResult.success("操作成功");
        }
    }

    @ApiOperation(value = "查看热门设备")
    @GetMapping("top")
    public CommonResult<List<TopDevice>> getTopDevice() {
        return CommonResult.success(topDeviceMapper.getTopDevice());
    }

    @Data
    static class DeviceVO {
        private Integer deviceId;
        private String imageUrl;
        private String name;
        private String status;
        private List<Property> propertyList;

        private List<Appendix> appendixList;
        private Laboratory laboratory;
    }
}
