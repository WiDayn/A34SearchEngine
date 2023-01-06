package cn.edu.hhu.a34backend.exception;

import cn.edu.hhu.a34backend.vo.StatusEnum;

public class CustomException extends RuntimeException
{
    protected StatusEnum statusEnum;

    public CustomException(StatusEnum statusEnum)
    {
        this.statusEnum = statusEnum;
    }

    public StatusEnum getErrorCode()
    {
        return statusEnum;
    }
}
