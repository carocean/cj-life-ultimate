package cj.life.ability.api;

import cj.life.ability.api.exception.ApiException;
import cj.life.ability.api.exception.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;

/**
 * 全局异常处理类
 *
 * @author sw-code
 * @RestControllerAdvice(@ControllerAdvice)，拦截异常并统一处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理自定义的业务异常
     *
     * @param e       异常对象
     * @param request request
     * @return 错误结果
     */
    @ExceptionHandler(ApiException.class)
    public ErrorResult bizExceptionHandler(ApiException e, HttpServletRequest request) {
        log.error("发生业务异常！原因是: {}", e.getMessage());
        return ErrorResult.fail(e.getCode(), e.getMessage(), e);
    }

    // 拦截抛出的异常，@ResponseStatus：用来改变响应状态码
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public ErrorResult handlerThrowable(Throwable e, HttpServletRequest request) {
        log.error("发生未知异常！原因是: ", e);
        ErrorResult error = ErrorResult.fail(ResultCode.SYSTEM_ERROR, e);
        return error;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileNotFoundException.class)
    public ErrorResult handlerNotFoundException(FileNotFoundException e, HttpServletRequest request) {
        log.error("404！原因是: ", e);
        ErrorResult error = ErrorResult.fail(ResultCode.NOTFOUND_ERROR, e);
        return error;
    }

    // 参数校验异常
    @ExceptionHandler(BindException.class)
    public ErrorResult handleBindExcpetion(BindException e, HttpServletRequest request) {
        log.error("发生参数校验异常！原因是：", e);
        ErrorResult error = ErrorResult.fail(ResultCode.PARAM_IS_INVALID, e, e.getAllErrors().get(0).getDefaultMessage());
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("发生参数校验异常！原因是：", e);
        ErrorResult error = ErrorResult.fail(ResultCode.PARAM_IS_INVALID, e, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return error;
    }
}
