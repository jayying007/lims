package edu.scnu.lims.service;

import edu.scnu.lims.dao.DeviceRecordDao;
import edu.scnu.lims.entity.DeviceRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ZhuangJieYing
 */
@Slf4j
@Service
public class DeviceRecordService {
    @Resource
    private DeviceRecordDao deviceRecordDao;

    public DeviceRecord saveRecord(DeviceRecord deviceRecord) {
        return deviceRecordDao.save(deviceRecord);
    }

    public List<DeviceRecord> getRecords(int page, int pageSize, DeviceRecord deviceRecord) {
        Example<DeviceRecord> example = Example.of(deviceRecord);

        return deviceRecordDao.findAll(example, PageRequest.of(page, pageSize)).toList();
    }
}
