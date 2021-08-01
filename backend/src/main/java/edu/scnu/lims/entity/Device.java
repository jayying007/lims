package edu.scnu.lims.entity;

import edu.scnu.lims.constant.DeviceStatusEnum;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author ZhuangJieYing
 */
@Data
@Entity
@Table(name = "device")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Integer deviceId;
    private String imageUrl;
    private String name;
    @Enumerated(EnumType.STRING)
    private DeviceStatusEnum status;

    /**
     * cascade = CascadeType.ALL 应该是保证Device写入后,获取其ID,再写入Property
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "device_id")
    private List<Property> propertyList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "device_id")
    private List<Appendix> appendixList;

    @ManyToOne
    @JoinColumn(name = "laboratory_id")
    private Laboratory laboratory;
}
