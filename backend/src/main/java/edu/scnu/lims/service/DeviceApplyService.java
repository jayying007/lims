package edu.scnu.lims.service;

import edu.scnu.lims.constant.DeviceApplyStatusEnum;
import edu.scnu.lims.constant.DeviceStatusEnum;
import edu.scnu.lims.constant.UserRoleEnum;
import edu.scnu.lims.dao.DeviceApplyDao;
import edu.scnu.lims.entity.Device;
import edu.scnu.lims.entity.DeviceApply;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ZhuangJieYing
 */
@Slf4j
@Service
public class DeviceApplyService {
    @Resource
    private DeviceService deviceService;
    @Resource
    private DeviceApplyDao deviceApplyDao;

    /**
     * 保存申请单,同时修改对应设备的状态
     * @param deviceApply 申请单
     * @param device 关联的设备
     * @return 生成的申请单
     */
    @Transactional(rollbackFor = Exception.class)
    public DeviceApply saveDeviceApply(DeviceApply deviceApply, Device device) {
        device.setStatus(DeviceStatusEnum.BORROWED);
        deviceService.updateDevice(device);
        //第一次创建
        if(deviceApply.getApplyTimestamp() == null) {
            deviceApply.setApplyTimestamp(System.currentTimeMillis());
        }
        return deviceApplyDao.save(deviceApply);
    }

    public DeviceApply getDeviceApplyById(Integer deviceApplyId) {
        return deviceApplyDao.findById(deviceApplyId).orElse(null);
    }

    public List<DeviceApply> getDeviceApplies(int page, int pageSize, DeviceApply deviceApply) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("device.name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<DeviceApply> example = Example.of(deviceApply, matcher);

        return deviceApplyDao.findAll(example, PageRequest.of(page, pageSize)).toList();
    }

    public Long countDeviceApplies(DeviceApply deviceApply) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("device.name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<DeviceApply> example = Example.of(deviceApply, matcher);

        return deviceApplyDao.count(example);
    }

    public List<DeviceApply> getDeviceAppliesByUserIdOrLaboratoryId(int page, int pageSize, Integer userId, Integer laboratoryId) {
        return deviceApplyDao.findByUser_UserIdOrDevice_Laboratory_LaboratoryId(userId, laboratoryId, PageRequest.of(page, pageSize)).toList();
    }

    public Long countDeviceAppliesByUserIdOrLaboratoryId(Integer userId, Integer laboratoryId) {
        return deviceApplyDao.countByUser_UserIdOrDevice_Laboratory_LaboratoryId(userId, laboratoryId);
    }
    /**
     *
     * @param deviceApply
     * @param status 若为Canceled或Denied或Finished,则需恢复设备使用权
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public DeviceApply updateApply(DeviceApply deviceApply, DeviceApplyStatusEnum status, UserRoleEnum role) {
        switch (role) {
            case SUPER_ADMIN:
                if(status == DeviceApplyStatusEnum.CANCELED && deviceApply.getStatus() == DeviceApplyStatusEnum.APPLIED) {
                    deviceApply.setStatus(status);
                    //恢复设备状态
                    Device device = deviceApply.getDevice();
                    device.setStatus(DeviceStatusEnum.AVAILABLE);
                    deviceService.saveDevice(device);
                } else if(status == DeviceApplyStatusEnum.GRANTED && deviceApply.getStatus() == DeviceApplyStatusEnum.APPLIED) {
                    deviceApply.setStatus(status);
                    deviceApply.setGrantTimestamp(System.currentTimeMillis());
                } else if(status == DeviceApplyStatusEnum.DENIED && deviceApply.getStatus() == DeviceApplyStatusEnum.APPLIED) {
                    deviceApply.setStatus(status);
                    //恢复设备状态
                    Device device = deviceApply.getDevice();
                    device.setStatus(DeviceStatusEnum.AVAILABLE);
                    deviceService.saveDevice(device);
                } else if(status == DeviceApplyStatusEnum.RETURNED && deviceApply.getStatus() == DeviceApplyStatusEnum.GRANTED) {
                    deviceApply.setStatus(status);
                    deviceApply.setReturnTimestamp(System.currentTimeMillis());
                } else if(status == DeviceApplyStatusEnum.FINISHED && deviceApply.getStatus() == DeviceApplyStatusEnum.RETURNED) {
                    deviceApply.setStatus(status);
                    deviceApply.setFinishTimestamp(System.currentTimeMillis());
                    //恢复设备状态
                    Device device = deviceApply.getDevice();
                    device.setStatus(DeviceStatusEnum.AVAILABLE);
                    deviceService.saveDevice(device);
                }
                break;
            case ADMIN:
                if(status == DeviceApplyStatusEnum.GRANTED && deviceApply.getStatus() == DeviceApplyStatusEnum.APPLIED) {
                    deviceApply.setStatus(status);
                    deviceApply.setGrantTimestamp(System.currentTimeMillis());
                } else if(status == DeviceApplyStatusEnum.DENIED && deviceApply.getStatus() == DeviceApplyStatusEnum.APPLIED) {
                    deviceApply.setStatus(status);
                    //恢复设备状态
                    Device device = deviceApply.getDevice();
                    device.setStatus(DeviceStatusEnum.AVAILABLE);
                    deviceService.saveDevice(device);
                } else if(status == DeviceApplyStatusEnum.FINISHED && deviceApply.getStatus() == DeviceApplyStatusEnum.RETURNED) {
                    deviceApply.setStatus(status);
                    deviceApply.setFinishTimestamp(System.currentTimeMillis());
                    //恢复设备状态
                    Device device = deviceApply.getDevice();
                    device.setStatus(DeviceStatusEnum.AVAILABLE);
                    deviceService.saveDevice(device);
                }
                break;
            case NORMAL:
                if(status == DeviceApplyStatusEnum.CANCELED && deviceApply.getStatus() == DeviceApplyStatusEnum.APPLIED) {
                    deviceApply.setStatus(status);
                } else if(status == DeviceApplyStatusEnum.RETURNED && deviceApply.getStatus() == DeviceApplyStatusEnum.GRANTED) {
                    deviceApply.setStatus(status);
                    deviceApply.setReturnTimestamp(System.currentTimeMillis());
                }
                break;
            default:
        }

        return deviceApplyDao.save(deviceApply);
    }

}
