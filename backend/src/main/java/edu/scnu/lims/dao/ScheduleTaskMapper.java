package edu.scnu.lims.dao;

import edu.scnu.lims.entity.RemindObject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author jay
 * @since 2021/4/8
 */
@Mapper
public interface ScheduleTaskMapper {

    List<RemindObject> getRemindRecord();

    List<RemindObject> getRemindReturn();
}
