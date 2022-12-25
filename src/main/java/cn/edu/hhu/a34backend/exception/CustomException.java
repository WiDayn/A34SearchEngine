package cn.edu.hhu.a34backend.exception;

import cn.edu.hhu.a34backend.vo.ErrorCode;

public class CustomException extends RuntimeException
{
    protected ErrorCode errorCode;

    public CustomException(ErrorCode errorCode)
    {
        this.errorCode=errorCode;
    }

    public ErrorCode getErrorCode()
    {
        return errorCode;
    }
}
