package edu.scnu.lims.constant;

/**
 * @author ZhuangJieYing
 */
public enum DeviceApplyStatusEnum {
    /**
     * 设备申请状态
     */
    APPLIED("已申请"),
    GRANTED("已同意"),
    RETURNED("已归还"),
    FINISHED("已完成"),
    CANCELED("已取消"),
    DENIED("已拒绝");

    String value;

    DeviceApplyStatusEnum(String value) {
        this.value = value;
    }
}
