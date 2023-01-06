package cn.edu.hhu.a34searchengine.vo;

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
        return new Result(true,StatusEnum.OPERATION_OK.code, data,msg);
    }

    public static Result success(Object data){
        return new Result(true,StatusEnum.OPERATION_OK.code,data,StatusEnum.OPERATION_OK.msg);
    }

    public static Result fail(int code,String msg){
        return new Result(false,code,null,msg);
    }

    public static Result fail(int code,String msg,Object data){
        return new Result(false,code,data,msg);
    }

    public static Result fail(StatusEnum statusEnum){
        return new Result(false, statusEnum.code,null, statusEnum.msg);
    }


}
