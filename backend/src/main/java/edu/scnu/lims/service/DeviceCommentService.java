package edu.scnu.lims.service;

import edu.scnu.lims.dao.DeviceCommentDao;
import edu.scnu.lims.entity.DeviceComment;
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
public class DeviceCommentService {
    @Resource
    private DeviceCommentDao deviceCommentDao;

    public DeviceComment saveComment(DeviceComment deviceComment) {
        return deviceCommentDao.save(deviceComment);
    }

    public List<DeviceComment> getComments(int page, int pageSize, DeviceComment deviceComment) {
        Example<DeviceComment> example = Example.of(deviceComment);

        return deviceCommentDao.findAll(example, PageRequest.of(page, pageSize)).toList();
    }
}
