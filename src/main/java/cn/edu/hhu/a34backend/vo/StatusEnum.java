package cn.edu.hhu.a34backend.vo;

public enum StatusEnum
{
    OPERATION_OK(0,"操作成功"),
    INVALID_ARGUMENT(1,"参数有误或无效"),
    SYSTEM_EXCEPTION(2,"系统异常"),
    ACCOUNT_NOT_EXIST(10001,"用户名不存在"),
    WRONG_PASSWORD(10002,"密码错误"),
    TOKEN_ERROR(10003,"token不合法"),
    TOKEN_EXPIRED(10004,"token已失效"),

    NO_PERMISSION(70001,"无访问权限"),
    SESSION_TIME_OUT(90001,"会话超时"),
    ACCOUNT_EXIST(90002,"账号已经存在"),
    NO_LOGIN(90002,"未登录"),
    CANT_DELETE(90003,"管理员和超级管理员无法删除"),
    FIND_ERROR(90004,"查找失败");


    private int code;
    private String msg;

    StatusEnum(int code, String msg){
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
