package cn.edu.hhu.a34backend.vo;

public enum ErrorCode {
    PARAMS_ERROR(10001,"参数有误"),
    PARAMS_EMPTY(1000,"参数为空"),
    ACCOUNT_PWD_NOT_EXIST(10002,"用户名或密码不存在"),
    TOKEN_ERROR(10003,"token不合法"),
    SYSTEM_EXCEPTION(10004,"系统异常"),
    NO_PERMISSION(70001,"无访问权限"),
    SESSION_TIME_OUT(90001,"会话超时"),
    ACCOUNT_EXIST(90002,"账号已经存在"),
    NO_LOGIN(90002,"未登录"),
    CANT_DELETE(90003,"管理员和超级管理员无法删除"),
    FIND_ERROR(90004,"查找失败")
    ;


    private int code;
    private String msg;

    ErrorCode(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
