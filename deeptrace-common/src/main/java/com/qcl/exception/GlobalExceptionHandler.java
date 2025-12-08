package com.qcl.exception;

import com.alibaba.druid.util.StringUtils;
import com.qcl.api.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLSyntaxErrorException;

/**
 * 全局异常处理类
 * Created by macro on 2020/2/27.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = ApiException.class)
    public Result handle(ApiException e) {
        if (e.getErrorCode() != null) {
            return Result.error(e.getErrorCode());
        }
        return Result.error(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = BindException.class)
    public Result handleValidException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = null;
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getField()+fieldError.getDefaultMessage();
            }
        }
        return Result.error(message);
    }

    @ResponseBody
    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public Result handleSQLSyntaxErrorException(SQLSyntaxErrorException e) {
        String message = e.getMessage();
        if (!StringUtils.isEmpty(message) && message.contains("denied")) {
            message = "演示环境暂无修改权限，如需修改数据可本地搭建后台服务！";
        }
        return Result.error(message);
    }
}
