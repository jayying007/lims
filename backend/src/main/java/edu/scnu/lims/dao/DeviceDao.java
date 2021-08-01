package edu.scnu.lims.dao;

import edu.scnu.lims.entity.Device;
import edu.scnu.lims.entity.TopDevice;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ZhuangJieYing
 */
public interface DeviceDao extends JpaRepository<Device,Integer> {
}
