package edu.scnu.lims.dao;

import edu.scnu.lims.entity.DeviceApply;
import edu.scnu.lims.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author jay
 * @since 2021/4/9
 */
@Mapper
public interface DeviceApplyMapper {
    List<DeviceApply> findDeviceApply(User user);
}
