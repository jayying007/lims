package edu.scnu.lims.entity;

import lombok.Data;

/**
 * @author jay
 * @since 2021/4/9
 */
@Data
public class TopDevice {
    private Integer deviceId;
    private String deviceName;
    private String laboratoryName;
    private Integer applyCount;
}
