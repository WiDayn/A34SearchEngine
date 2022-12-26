package cn.edu.hhu.a34backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class Result implements Serializable {
    private boolean success;

    private int code;

    private Object data;

    private String message;

    public static Result success(Object data,String msg){
        return new Result(true,200,data,msg);
    }

    public static Result fail(int code,String msg){
        return new Result(false,code,null,msg);
    }

    public static Result fail(int code,String msg,Object data){
        return new Result(false,code,data,msg);
    }

    public static Result fail(ErrorCode errorCode){
        return new Result(false,errorCode.getCode(),null,errorCode.getMsg());
    }


}
