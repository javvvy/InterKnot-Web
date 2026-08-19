package top.hzwhzw.iwchatservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pojo.Result;
import top.hzwhzw.iwcommon.exception.BaseExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public Result onIllegalArgument(IllegalArgumentException e) {
        return super.handleIllegalArgument(e);
    }

    @ExceptionHandler(Exception.class)
    public Result onException(Exception e) {
        log.error("服务异常,请联系管理员,异常信息:{}", e.getMessage());
        return super.handleUnexpected(e);
    }
}
