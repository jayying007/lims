package edu.scnu.lims.dao;


import edu.scnu.lims.entity.Laboratory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ZhuangJieYing
 */
public interface LaboratoryDao extends JpaRepository<Laboratory,Integer> {
}
