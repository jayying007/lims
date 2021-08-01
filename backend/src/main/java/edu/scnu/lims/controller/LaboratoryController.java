package edu.scnu.lims.controller;

import edu.scnu.lims.annotation.LoginRequired;
import edu.scnu.lims.common.CommonResult;
import edu.scnu.lims.constant.UserRoleEnum;
import edu.scnu.lims.entity.Device;
import edu.scnu.lims.entity.Laboratory;
import edu.scnu.lims.entity.User;
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


/**
 * @author ZhuangJieYing
 */
@Api(tags = "实验室管理")
@Slf4j
@RestController
@RequestMapping("laboratory")
public class LaboratoryController {
    @Resource
    private LaboratoryService laboratoryService;
    @Resource
    private DeviceService deviceService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @LoginRequired
    @ApiOperation(value = "添加实验室", notes = "只有ADMIN及以上级别才能执行该接口")
    @PostMapping
    public CommonResult<Laboratory> addLaboratory(@RequestHeader(value = "Authentication-Token") String token,
                                                  @RequestBody @Valid LaboratoryVO laboratoryVO) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }

        if(UserRoleEnum.NORMAL == user.getRole()) {
            return CommonResult.forbidden(null);
        } else {
            Laboratory laboratory = new Laboratory();
            laboratory.setName(laboratoryVO.getName());
            laboratory.setLocation(laboratoryVO.getLocation());
            laboratory.setContact(laboratoryVO.getContact());
            laboratory.setUser(user);

            return CommonResult.success(laboratoryService.saveLaboratory(laboratory));
        }
    }

    @LoginRequired
    @ApiOperation(value = "查询实验室信息", notes = "可配合参数作为高级检索")
    @GetMapping("list")
    public CommonResult<List<Laboratory>> getList(@RequestParam Integer page, @RequestParam Integer pageSize,
                                                  @RequestParam(required = false) String name) {
        Laboratory laboratory = new Laboratory();
        laboratory.setName(name);

        return CommonResult.success(laboratoryService.listLaboratories(page, pageSize, laboratory));
    }

    @LoginRequired
    @ApiOperation(value = "查询某个用户创建的所有实验室")
    @GetMapping("list/user")
    public CommonResult<List<Laboratory>> getListByUserId(@RequestHeader(value = "Authentication-Token") String token,
                                                          @RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "50") Integer pageSize) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }

        Laboratory laboratory = new Laboratory();
        laboratory.setUser(user);

        return CommonResult.success(laboratoryService.listLaboratories(page, pageSize, laboratory));
    }

    @LoginRequired
    @ApiOperation(value = "统计实验室数量")
    @GetMapping("list/count")
    public CommonResult<Long> getLaboratoryCount(@RequestParam(required = false) String name) {
        Laboratory laboratory = new Laboratory();
        laboratory.setName(name);

        return CommonResult.success(laboratoryService.countLaboratories(laboratory));
    }

    @LoginRequired
    @ApiOperation(value = "更新实验室信息", notes = "要求ADMIN及以上级别,且ADMIN只能更新自己创建的")
    @PutMapping
    public CommonResult<Laboratory> updateLaboratory(@RequestHeader(value = "Authentication-Token") String token,
                                                   @RequestBody LaboratoryVO laboratoryVO) {
        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }
        Laboratory laboratory = new Laboratory();
        //需要判断修改的是不是自己的
        laboratory.setLaboratoryId(laboratoryVO.getLaboratoryId());
        Laboratory oldLab = laboratoryService.getLaboratory(laboratory);

        switch (user.getRole()) {
            case NORMAL:
                return CommonResult.forbidden(null);
            case ADMIN:
                if(oldLab != null && oldLab.getUser().getUserId().equals(user.getUserId())) {
                    laboratory.setName(laboratoryVO.getName());
                    laboratory.setLocation(laboratoryVO.getLocation());
                    laboratory.setContact(laboratoryVO.getContact());
                    laboratory.setUser(oldLab.getUser());

                    return CommonResult.success(laboratoryService.updateLaboratory(laboratory));
                }

                return CommonResult.forbidden(null);
            case SUPER_ADMIN:
                laboratory.setLaboratoryId(laboratoryVO.getLaboratoryId());
                laboratory.setName(laboratoryVO.getName());
                laboratory.setLocation(laboratoryVO.getLocation());
                laboratory.setContact(laboratoryVO.getContact());
                laboratory.setUser(oldLab.getUser());

                return CommonResult.success(laboratoryService.updateLaboratory(laboratory));
            default:
                return CommonResult.failed("出错了");
        }
    }

    @LoginRequired
    @ApiOperation(value = "删除实验室", notes = "要求ADMIN及以上级别,且ADMIN只能删除自己创建的; 如果有设备关联,则无法删除")
    @DeleteMapping
    public CommonResult<String> deleteLaboratory(@RequestHeader(value = "Authentication-Token") String token,
                                                 @RequestParam Integer laboratoryId) {
        Device device = new Device();
        Laboratory laboratory = new Laboratory();
        laboratory.setLaboratoryId(laboratoryId);
        device.setLaboratory(laboratory);
        if(deviceService.getDevice(device) != null) {
            return CommonResult.failed("存在设备关联");
        }

        String userSerialize = (String)redisTemplate.opsForValue().get(token);
        User user = GsonUtils.fromJson(userSerialize, User.class);
        if(user == null) {
            return CommonResult.expire();
        }

        Laboratory oldLab = laboratoryService.getLaboratory(laboratory);

        switch (user.getRole()) {
            case NORMAL:
                return CommonResult.forbidden(null);
            case ADMIN:
                if(oldLab != null && oldLab.getUser().getUserId().equals(user.getUserId())) {
                    laboratoryService.deleteLaboratory(laboratoryId);

                    return CommonResult.success(null);
                }
                return CommonResult.failed("实验室不存在或权限不足");
            case SUPER_ADMIN:
                laboratoryService.deleteLaboratory(laboratoryId);

                return CommonResult.success(null);
            default:
                return CommonResult.failed("出错了");
        }
    }

    @Data
    static class LaboratoryVO {
        private Integer laboratoryId;
        private String name;
        private String location;
        private String contact;
    }
}
