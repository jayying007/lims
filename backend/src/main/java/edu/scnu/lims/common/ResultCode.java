package edu.scnu.lims.common;

/**
 * @author jane
 */

public enum ResultCode {
    /**
     *
     */
    SUCCESS(200, "操作成功"),
    EXPIRE(40000, "token已过期"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    VALIDATE_FAILED(404, "参数检验失败"),
    FAILED(500, "操作失败");

    private final long code;
    private final String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
