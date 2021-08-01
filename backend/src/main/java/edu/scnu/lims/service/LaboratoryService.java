package edu.scnu.lims.service;

import edu.scnu.lims.dao.LaboratoryDao;
import edu.scnu.lims.entity.Laboratory;
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
public class LaboratoryService {
    @Resource
    private LaboratoryDao laboratoryDao;

    public Laboratory saveLaboratory(Laboratory laboratory) {
        return laboratoryDao.save(laboratory);
    }

    public Laboratory getLaboratory(Laboratory laboratory) {
        Example<Laboratory> example = Example.of(laboratory);
        List<Laboratory> res = laboratoryDao.findAll(example);
        if(res.size() > 0) {
            return res.get(0);
        }
        return null;
    }

    public List<Laboratory> listLaboratories(int page, int pageSize, Laboratory laboratory) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Laboratory> example = Example.of(laboratory, matcher);

        return laboratoryDao.findAll(example, PageRequest.of(page, pageSize)).toList();
    }

    public Long countLaboratories(Laboratory laboratory) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains());
        Example<Laboratory> example = Example.of(laboratory, matcher);

        return laboratoryDao.count(example);
    }

    public Laboratory updateLaboratory(Laboratory laboratory) {
        Optional<Laboratory> oldLab = laboratoryDao.findById(laboratory.getLaboratoryId());
        if(oldLab.isPresent()) {
            Laboratory newLab = oldLab.get();
            if(laboratory.getName() != null) {
                newLab.setName(laboratory.getName());
            }
            if(laboratory.getLocation() != null) {
                newLab.setLocation(laboratory.getLocation());
            }
            if(laboratory.getContact() != null) {
                newLab.setContact(laboratory.getContact());
            }
            return laboratoryDao.save(newLab);
        }
        return null;
    }

    public void deleteLaboratory(Integer laboratoryId) {
        laboratoryDao.deleteById(laboratoryId);
    }
}
