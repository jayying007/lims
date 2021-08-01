package edu.scnu.lims.service;

import edu.scnu.lims.dao.DeviceDao;
import edu.scnu.lims.entity.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author ZhuangJieYing
 */
@Slf4j
@Service
public class DeviceService {
    @Resource
    private DeviceDao deviceDao;

    public Device saveDevice(Device device) {
        return deviceDao.save(device);
    }

    public Device getDevice(Device device) {
        Example<Device> example = Example.of(device);
        Optional<Device> res = deviceDao.findOne(example);

        return res.orElse(null);
    }

    public Device getDeviceById(Integer deviceId) {
        return deviceDao.findById(deviceId).orElse(null);
    }

    public List<Device> getDevices(Integer page, Integer pageSize, Device device) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Device> example = Example.of(device, matcher);

        return deviceDao.findAll(example, PageRequest.of(page, pageSize)).toList();
    }

    public Long countDevices(Device device) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Device> example = Example.of(device, matcher);

        return deviceDao.count(example);
    }

    public Device updateDevice(Device device) {
        log.info("更新设备信息" + device);
        return deviceDao.save(device);
    }

    public void deleteDeviceById(Integer deviceId) {
        deviceDao.deleteById(deviceId);
    }
}
