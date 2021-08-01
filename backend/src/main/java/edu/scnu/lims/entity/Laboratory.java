package edu.scnu.lims.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author ZhuangJieYing
 */
@Data
@Entity
@Table(name = "laboratory")
public class Laboratory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "laboratory_id")
    private Integer laboratoryId;
    private String name;
    private String location;
    private String contact;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany
    @JoinColumn(name = "laboratory_id")
    private transient List<Device> deviceList;
}
