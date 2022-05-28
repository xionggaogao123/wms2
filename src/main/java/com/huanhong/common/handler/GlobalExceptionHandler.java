package com.huanhong.common.handler;


import cn.hutool.core.exceptions.ValidateException;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @version v1.0
 * @since 2018/5/17 20:38
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * javax.validation参数验证异常处理
     *
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<Object> methodArgumentNotValidHandler(HttpServletResponse response, MethodArgumentNotValidException e) {
        Result<Object> result = new Result<>();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        if (fieldErrors.size() > 1) {
            List<Map<String, Object>> errorList = new ArrayList<>();
            fieldErrors.forEach(itme -> {
                // 解析原错误信息，封装后返回，此处返回非法的字段名称，原始值，错误信息
                Map<String, Object> error = new HashMap<>(3);
                error.put("field", itme.getField());
                error.put("message", itme.getDefaultMessage());
                error.put("value", itme.getRejectedValue());
                errorList.add(error);
            });
            result.setData(errorList);
            result.setMessage("多个参数错误");
        } else {
            result.setData(fieldErrors.get(0).getField());
            result.setMessage(fieldErrors.get(0).getDefaultMessage());
        }
        result.setStatus(ErrorCode.PARAM_ERROR);
        return result;
    }

    /**
     * hutool validation参数验证异常处理
     *
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = ValidateException.class)
    public Result<String> validateException(HttpServletResponse response, ValidateException e) {
        Result<String> result = new Result<>();
        response.setStatus(200);
        result.setMessage(e.getLocalizedMessage());
        result.setStatus(ErrorCode.PARAM_ERROR);
        return result;
    }

    @ExceptionHandler(value = RuntimeException.class)
    public Result<String> runtimeException(HttpServletResponse response, RuntimeException e) {
        log.error("RuntimeException",e);
        Result<String> result = new Result<>();
        response.setStatus(200);
        result.setMessage(e.getMessage());
        result.setStatus(ErrorCode.SYSTEM_ERROR);
        return result;
    }

    @ExceptionHandler(value = Exception.class)
    public Result<String> exception(HttpServletResponse response, Exception e) {
        log.error("Exception",e);
        Result<String> result = new Result<>();
        response.setStatus(200);
        result.setMessage(e.getMessage());
        result.setStatus(ErrorCode.SYSTEM_ERROR);
        return result;
    }

}