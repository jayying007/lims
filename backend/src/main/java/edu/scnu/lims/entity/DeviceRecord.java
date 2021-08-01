package edu.scnu.lims.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhuangJieYing
 */
@Data
@Entity
@Table(name = "device_record")
public class DeviceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deviceRecordId;
    private Long recordTimestamp;
    private String recordImageUrl;
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;
}
