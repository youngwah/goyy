package com.realgo.common.utils.exception;

import com.realgo.common.utils.result.ResultCodeEnum;
import io.swagger.annotations.ApiModelProperty;

public class GoyyException extends RuntimeException {

    @ApiModelProperty(value = "异常状态码")
    private Integer code;

    public GoyyException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public GoyyException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }

    @Override
    public String toString() {
        return "GoyyException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
