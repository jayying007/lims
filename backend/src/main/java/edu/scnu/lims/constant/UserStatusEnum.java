package edu.scnu.lims.constant;

/**
 * @author ZhuangJieYing
 */
public enum  UserStatusEnum {
    /**
     * 用户状态,判断该账号是否可用
     */
    ENABLE("可用"),
    DISABLE("禁用");

    String value;

    UserStatusEnum(String value) {
        this.value = value;
    }
}
