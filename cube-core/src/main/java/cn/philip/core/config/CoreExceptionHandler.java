package cn.philip.core.config;

import cn.philip.common.entity.CubeResult;
import cn.philip.common.exception.CubeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description:
 * @author: pfliu
 * @time: 2020/4/1
 */
@Slf4j
@ControllerAdvice
public class CoreExceptionHandler {

    @ExceptionHandler(value = CubeException.class)
    @ResponseBody
    public CubeResult bizExceptionHandler(CubeException exp) {
        log.error(exp.getMessage(), exp);
        return new CubeResult(exp.getCode(), exp.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CubeResult exceptionHandler(Exception exp) {
        log.error(exp.getMessage(), exp);
        return new CubeResult(500, "系统错误");
    }
}
