package edu.scnu.lims.constant;

/**
 * @author ZhuangJieYing
 */
public enum DeviceStatusEnum {
    /**
     * 判断设备的状态
     */
    AVAILABLE("可用"),
    BORROWED("借出"),
    REPAIRING("修理中"),
    DAMAGED("损坏");

    String value;

    DeviceStatusEnum(String value) {
        this.value = value;
    }
}
