package cn.edu.hhu.a34backend.vo;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
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
}
