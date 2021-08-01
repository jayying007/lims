package edu.scnu.lims.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhuangJieYing
 */
@Data
@Entity
@Table(name = "device_comment")
public class DeviceComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deviceCommentId;
    private String comment;
    private Long commentTimestamp;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
}
