package edu.scnu.lims.controller.device;

import edu.scnu.lims.annotation.LoginRequired;
import edu.scnu.lims.common.CommonResult;
import edu.scnu.lims.constant.DeviceApplyStatusEnum;
import edu.scnu.lims.constant.DeviceStatusEnum;
import edu.scnu.lims.constant.UserRoleEnum;
import edu.scnu.lims.dao.DeviceApplyDao;
import edu.scnu.lims.dao.DeviceApplyMapper;
import edu.scnu.lims.entity.Device;
import edu.scnu.lims.entity.DeviceApply;
import edu.scnu.lims.entity.Laboratory;
import edu.scnu.lims.entity.User;
import edu.scnu.lims.service.DeviceApplyService;
import edu.scnu.lims.service.DeviceService;
import edu.scnu.lims.service.LaboratoryService;
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
import java.util.Objects;

/**
 * @author ZhuangJieYing
 */
@Slf4j
@Api(tags = "设备申请")
@RestController
@RequestMapping("device")
public class ApplyController {
    @Resource
    private DeviceService deviceService;
    @Resource
    private DeviceApplyService deviceApplyService;
    @Resource
    private DeviceApplyMapper deviceApplyMapper;
    @Resource
    private LaboratoryService laboratoryService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @LoginRequired
    @ApiOperation(value = "提交设备申请")
    @PostMapping("apply")
    public CommonResult<DeviceApply> applyDevice(@RequestHeader(value = "Authentication-Token") String token,
                                                 @RequestBody @Valid DeviceApplyVO deviceApplyVO) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }

        synchronized (this) {
            //判断设备是否被借了,同时要防止多人竞争
            Integer deviceId = deviceApplyVO.getDevice().getDeviceId();
            Device device = deviceService.getDeviceById(deviceId);

            if(device != null && DeviceStatusEnum.AVAILABLE == device.getStatus()) {
                DeviceApply deviceApply = new DeviceApply();
                deviceApply.setDevice(deviceApplyVO.getDevice());
                deviceApply.setUser(user);
                deviceApply.setStatus(DeviceApplyStatusEnum.APPLIED);
                deviceApply.setBorrowReason(deviceApplyVO.borrowReason);
                deviceApply.setPromiseTimestamp(deviceApplyVO.promiseTimestamp);

                return CommonResult.success(deviceApplyService.saveDeviceApply(deviceApply, device));
            }
        }
        return CommonResult.failed("失效");
    }

    /**
     * 这里查询由两部分集成, 1) 用户申请的设备 2) 若为管理员,则包含待审查的申请列表
     * @param token 用户标识
     * @param page 第几页,从0开始
     * @param pageSize 返回的数据量大小
     * @param name 高级检索
     * @param status 高级检索
     * @return 申请列表
     */
    @LoginRequired
    @ApiOperation(value = "查询设备申请表")
    @GetMapping("apply")
    public CommonResult<List<DeviceApply>> getApplyList(@RequestHeader(value = "Authentication-Token") String token,
                                                        @RequestParam Integer page, @RequestParam Integer pageSize,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) DeviceApplyStatusEnum status) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }

        DeviceApply deviceApply = new DeviceApply();
        Device device = new Device();
        device.setName(name);
        deviceApply.setDevice(device);
        deviceApply.setStatus(status);

        //超级管理员可以查看全部
        if(UserRoleEnum.SUPER_ADMIN == user.getRole()) {
            return CommonResult.success(deviceApplyService.getDeviceApplies(page, pageSize, deviceApply));
        } else {
            return CommonResult.success(deviceApplyMapper.findDeviceApply(user));
            /*
            Laboratory laboratory = new Laboratory();
            laboratory.setUser(user);
            Laboratory laboratory2 = laboratoryService.getLaboratory(laboratory);
            //如果存在,说明为ADMIN
            if(laboratory2 != null) {
                System.out.println("管理员啊");
                return CommonResult.success(deviceApplyService.getDeviceAppliesByUserIdOrLaboratoryId(page, pageSize,
                        user.getUserId(), laboratory2.getLaboratoryId()));
            } else {
                deviceApply.setUser(user);
                return CommonResult.success(deviceApplyService.getDeviceApplies(page, pageSize, deviceApply));
            }
            */
        }
    }

    @LoginRequired
    @ApiOperation(value = "统计设备申请表数量")
    @GetMapping("apply/list")
    public CommonResult<Long> countApplyList(@RequestHeader(value = "Authentication-Token") String token,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) DeviceApplyStatusEnum status) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }

        DeviceApply deviceApply = new DeviceApply();
        Device device = new Device();
        device.setName(name);
        deviceApply.setDevice(device);
        deviceApply.setStatus(status);

        //超级管理员可以查看全部
        if(UserRoleEnum.SUPER_ADMIN == user.getRole()) {
            return CommonResult.success(deviceApplyService.countDeviceApplies(deviceApply));
        } else {
            Laboratory laboratory = new Laboratory();
            laboratory.setUser(user);
            Laboratory laboratory2 = laboratoryService.getLaboratory(laboratory);
            //如果存在,说明为ADMIN
            if(laboratory2 != null) {
                return CommonResult.success(deviceApplyService.countDeviceAppliesByUserIdOrLaboratoryId(user.getUserId(),
                        laboratory2.getLaboratoryId()));
            } else {
                deviceApply.setUser(user);
                return CommonResult.success(deviceApplyService.countDeviceApplies(deviceApply));
            }
        }
    }

    /**
     *
     * @param token 用户标识
     * @param deviceApplyId 设备申请ID
     * @param status 待更新的设备状态
     * @return 更新后的信息
     */
    @LoginRequired
    @ApiOperation(value = "更新申请信息", notes = "管理员及所有者均可操作,但所处状态不同")
    @PutMapping("apply")
    public CommonResult<DeviceApply> updateApply(@RequestHeader(value = "Authentication-Token") String token,
                                                 @RequestParam Integer deviceApplyId,
                                                 @RequestParam DeviceApplyStatusEnum status) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }

        DeviceApply deviceApply = deviceApplyService.getDeviceApplyById(deviceApplyId);

        if(UserRoleEnum.SUPER_ADMIN == user.getRole()) {
            return CommonResult.success(deviceApplyService.updateApply(deviceApply, status, UserRoleEnum.SUPER_ADMIN));
        } else {
            //判断该申请ID是否属于用户,或者由该用户审查
            if(Objects.equals(deviceApply.getUser().getUserId(), user.getUserId())) {
                return CommonResult.success(deviceApplyService.updateApply(deviceApply, status, UserRoleEnum.NORMAL));
            }
            Laboratory laboratory = laboratoryService.getLaboratory(deviceApply.getDevice().getLaboratory());
            if(laboratory != null && Objects.equals(laboratory.getUser().getUserId(), user.getUserId())) {
                return CommonResult.success(deviceApplyService.updateApply(deviceApply, status, UserRoleEnum.ADMIN));
            }
            return CommonResult.forbidden(null);
        }
    }

    @Data
    static class DeviceApplyVO {
        private Integer deviceApplyId;
        private String borrowReason;
        private Long promiseTimestamp;

        private DeviceApplyStatusEnum status;

        private Device device;

        private User user;
    }
}
