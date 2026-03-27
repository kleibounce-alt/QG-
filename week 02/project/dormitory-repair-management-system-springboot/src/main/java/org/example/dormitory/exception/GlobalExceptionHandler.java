package org.example.dormitory.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.dormitory.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author klei
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务参数异常（如学号格式、密码错误等）
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("业务参数异常：{}", e.getMessage());
        // 状态码 400，错误信息直接返回给前端
        return Result.error(400, e.getMessage());
    }

    // 处理其他未捕获异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "服务器内部错误，请稍后再试");
    }
}