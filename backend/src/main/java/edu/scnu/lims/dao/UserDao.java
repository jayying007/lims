package edu.scnu.lims.dao;

import edu.scnu.lims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ZhuangJieYing
 */
public interface UserDao extends JpaRepository<User, Integer> {
}
