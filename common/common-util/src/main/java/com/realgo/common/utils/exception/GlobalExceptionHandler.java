package com.realgo.common.utils.exception;

import com.realgo.common.utils.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail();
    }


    @ResponseBody
    @ExceptionHandler(GoyyException.class)
    public Result error(GoyyException e) {
        e.printStackTrace();;
        return Result.fail();
    }
}
