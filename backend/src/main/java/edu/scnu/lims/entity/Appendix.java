package edu.scnu.lims.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

/**
 * @author ZhuangJieYing
 */
@Data
@Entity
@Table(name = "device_appendix")
@JsonIgnoreProperties({"device"})
public class Appendix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_appendix_id")
    private Integer deviceAppendixId;
    private String name;
    @ManyToOne
    @JoinColumn(name = "device_id")
    private transient Device device;
}
