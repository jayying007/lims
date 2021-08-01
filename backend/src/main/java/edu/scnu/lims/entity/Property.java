package edu.scnu.lims.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhuangJieYing
 */
@Data
@Entity
@Table(name = "device_property")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_property_id")
    private Integer devicePropertyId;
    private String name;
    private String value;
    @ManyToOne
    @JoinColumn(name = "device_id")
    private transient Device device;
}
