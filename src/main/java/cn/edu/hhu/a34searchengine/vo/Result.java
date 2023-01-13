package cn.edu.hhu.a34searchengine.vo;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class Result implements Serializable {

    @JsonView(ResultView.class)
    private boolean success;

    @JsonView(ResultView.class)
    private int code;

    @JsonView(ResultView.class)
    private Object data;

    @JsonView(ResultView.class)
    private String message;

    public interface ResultView{}

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
