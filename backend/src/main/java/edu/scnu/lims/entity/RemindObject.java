package edu.scnu.lims.entity;

import lombok.Data;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author jay
 * @since 2021/4/8
 */
@Data
public class RemindObject implements Serializable {
    private String email;
    private String deviceName;
}
