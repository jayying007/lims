package edu.scnu.lims.dao;

import edu.scnu.lims.entity.TopDevice;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author jay
 * @since 2021/4/9
 */
@Mapper
public interface TopDeviceMapper {
    List<TopDevice> getTopDevice();
}
