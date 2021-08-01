package edu.scnu.lims.dao;

import edu.scnu.lims.entity.DeviceComment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ZhuangJieYing
 */
public interface DeviceCommentDao extends JpaRepository<DeviceComment, Integer> {
}
