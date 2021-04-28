package unnet.weixin.netdisk.aop;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import unnet.weixin.netdisk.entity.RestResult;
import unnet.weixin.netdisk.exception.RestException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(RestException.class)
    @ResponseBody
    public RestResult<?> handException(HttpServletRequest request, RestException ex) {
        RestResult<?> response;
        logger.error("Exception code:{},msg{}", ex.getErrorCodeAndMsg().getCode(), ex.getErrorCodeAndMsg().getMsg());
        response = new RestResult<>(ex.getErrorCodeAndMsg().getCode(), ex.getErrorCodeAndMsg().getMsg());
        return response;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody
    public RestResult<?> handException(HttpServletRequest request, Exception ex) {
        RestResult<?> response;
        logger.error("Exception error:{}", ex);
        response = new RestResult<>(4,"发生错误");
        return response;
    }

    // 捕捉AuthenticationException的异常
    @org.springframework.web.bind.annotation.ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public RestResult<?> handle401(AuthenticationException ex) {
        logger.error("Exception error:{}", ex);
        return new RestResult<>(401, "token认证失败");
    }

    // 捕捉shiro的异常
    @org.springframework.web.bind.annotation.ExceptionHandler(ShiroException.class)
    @ResponseBody
    public RestResult<?> handleShiroException(ShiroException ex) {
        logger.error("Exception error:{}", ex);
        return new RestResult<>(4, "没有权限");
    }
}
