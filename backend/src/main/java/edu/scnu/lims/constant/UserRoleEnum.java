package edu.scnu.lims.constant;

/**
 * @author ZhuangJieYing
 */
public enum UserRoleEnum {
    /**
     * 用户身份, 代表了不同权限
     */
    NORMAL("普通用户"),
    ADMIN("管理员"),
    SUPER_ADMIN("超级管理员");

    String value;

    UserRoleEnum(String value) {
        this.value = value;
    }

}
