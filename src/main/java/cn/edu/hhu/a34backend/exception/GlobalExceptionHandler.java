package cn.edu.hhu.a34backend.exception;

import cn.edu.hhu.a34backend.vo.ErrorCode;
import cn.edu.hhu.a34backend.vo.Result;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler
{
    @ExceptionHandler(value = CustomException.class)
    public Result customExceptionHandler(CustomException e)
    {

        log.error("发生自定义异常:" + e.errorCode.getMsg() + "(" + e.errorCode.getCode() + ")");
        return Result.fail(e.errorCode);
    }

    @ExceptionHandler(value = NullPointerException.class)
    public Result nullPointerExceptionHandler(NullPointerException e)
    {
        log.error("发生空指针异常:" + e.getMessage());
        e.printStackTrace();
        return Result.fail(ErrorCode.SYSTEM_EXCEPTION);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result methodArgumentExceptionHandler(MethodArgumentNotValidException e)
    {
        log.error("发生接口参数异常:"+e.getMessage());
        e.printStackTrace();
        return Result.fail(ErrorCode.PARAMS_ERROR);
    }

    @ExceptionHandler(value= SignatureException.class)
    public Result signatureExceptionHandler(SignatureException e)
    {
        log.warn("发生令牌异常:"+e.getMessage());
        e.printStackTrace();
        return Result.fail(ErrorCode.TOKEN_ERROR);
    }

    @ExceptionHandler(value= ExpiredJwtException.class)
    public Result signatureExceptionHandler(ExpiredJwtException e)
    {
        log.warn("发生令牌异常:"+e.getMessage());
        e.printStackTrace();
        return Result.fail(ErrorCode.TOKEN_EXPIRED);
    }

    @ExceptionHandler(value = Exception.class)
    public Result undefinedExceptionHandler(Exception e)
    {
        log.warn("发生其他异常:"+e.getMessage());
        e.printStackTrace();
        return Result.fail(ErrorCode.SYSTEM_EXCEPTION);
    }
}
