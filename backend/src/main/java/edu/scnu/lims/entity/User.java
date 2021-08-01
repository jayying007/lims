package edu.scnu.lims.entity;

import edu.scnu.lims.constant.UserRoleEnum;
import edu.scnu.lims.constant.UserStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author ZhuangJieYing
 */
@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRoleEnum role;

    @Column(name = "avatar")
    private String avatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatusEnum status;

    @OneToMany
    @JoinColumn(name = "user_id")
    private transient List<Laboratory> laboratoryList;
}
