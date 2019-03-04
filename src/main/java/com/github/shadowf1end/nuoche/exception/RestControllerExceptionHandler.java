package com.github.shadowf1end.nuoche.exception;

import com.github.shadowf1end.nuoche.common.util.ResultUtil;
import com.github.shadowf1end.nuoche.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Exrickx
 */
@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(WxErrorException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public Result<Object> handleWeChatException(WxErrorException e) {

//        String errorMsg = "WeChat exception";
        if (e != null) {
//            errorMsg = e.getMessage();
            log.error("", e);
        }
        return new ResultUtil<>().setErrorMsg(500, "网络错误请稍后再在试");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.OK)
    public Result<Object> handleException(Exception e) {

//        String errorMsg = "System Exception";
        if (e != null) {
//            errorMsg = e.getMessage();
            log.error("", e);
        }
        return new ResultUtil<>().setErrorMsg(500, "网络错误请稍后再在试");
    }
}