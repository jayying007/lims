package edu.scnu.lims.dao;

import edu.scnu.lims.entity.DeviceApply;
import edu.scnu.lims.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ZhuangJieYing
 */
public interface DeviceApplyDao extends JpaRepository<DeviceApply, Integer> {
    /**
     * 通过UserId或者LaboratoryId找出申请单, 可以一次性帮ADMIN找出自己申请的和需要自己审查的申请单
     * @param userId
     * @param laboratoryId
     * @param page
     * @return
     */
    Page<DeviceApply> findByUser_UserIdOrDevice_Laboratory_LaboratoryId(Integer userId, Integer laboratoryId, Pageable page);

    /**
     * 通过UserId或者LaboratoryId找出申请单, 可以一次性帮ADMIN找出自己申请的和需要自己审查的申请单
     * @param userId
     * @param laboratoryId
     * @return
     */
    Long countByUser_UserIdOrDevice_Laboratory_LaboratoryId(Integer userId, Integer laboratoryId);
}
