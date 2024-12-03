package com.zerostech.luminous.web.http.controller;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.zerostech.luminous.common.exception.ExceptionCode;
import com.zerostech.luminous.common.exception.ExpressionException;
import com.zerostech.luminous.common.exception.LuminousBizException;
import com.zerostech.luminous.common.response.Result;
import com.zerostech.luminous.web.Lu;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Objects;

/**
 * Created on 2022/7/13.
 *
 * @author 迹_Jason
 */

@Slf4j
@ControllerAdvice
@ResponseBody
@ConditionalOnProperty(prefix = "luminous.basic.http", name = "useUnityError", havingValue = "true", matchIfMissing = true)
public class ExceptionControllerAdvice {
    @Value(value = "${luminous.basic.http.exceptionDevelopMode:false}")
    private boolean exceptionDevelopMode;


    /**
     * 技术类型异常，有Zebra统一处理
     *
     * @param e        e
     * @param response 响应
     * @return {@link Result}<{@link Void}>
     */
    @ExceptionHandler(value = {MissingServletRequestParameterException.class, HttpRequestMethodNotSupportedException.class})
    public Result<Void> requestException(Exception e, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        String errMsg = e.getMessage();
        if (exceptionDevelopMode) {
            errMsg = parseExceptionTrace(e);
        }
        return Result.customize(Result.HTTP_REQUEST_ERROR, errMsg, Lu.isPresentTracing() ? Lu.basic.tracing.getTraceId() : null);
    }

    @ExceptionHandler(value = {ExpressionException.class, ConstraintViolationException.class})
    public Result<Void> patternException(Exception e, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        String errMsg = e.getMessage();
        if (exceptionDevelopMode) {
            errMsg = parseExceptionTrace(e);
        }
        return Result.customize(Result.PARAM_PATTERN_ERROR, errMsg, Lu.isPresentTracing() ? Lu.basic.tracing.getTraceId() : null);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public Result<Void> notFundException(Exception e, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        String errMsg = parseExceptionTrace(e);
        log.warn("参数错误: {}", errMsg);
        return Result.customize(Result.PARAM_NOT_FUND_ERROR, "参数错误", Lu.isPresentTracing() ? Lu.basic.tracing.getTraceId() : null);
    }

    /**
     * 业务异常处理，打印warn级别日志，默认不告警
     *
     * @param e
     * @param response
     * @return 返回值
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> methodArgumentException(MethodArgumentNotValidException e, HttpServletResponse response) {
        BindingResult bindingResult = e.getBindingResult();
        response.setContentType("application/json;charset=UTF-8");

        return Result.customize(Result.BAD_REQUEST, Objects.nonNull(bindingResult.getFieldError()) ?
                bindingResult.getFieldError().getDefaultMessage() : "参数校验错误", Lu.isPresentTracing() ? Lu.basic.tracing.getTraceId() : null);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
    public Result<Void> argumentValidException(RuntimeException e, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        return Result.customize(Result.BAD_REQUEST, e.getMessage(), Lu.isPresentTracing() ? Lu.basic.tracing.getTraceId() : null);
    }

    /**
     * 业务异常处理，打印warn级别日志，默认不告警
     *
     * @param e
     * @param response
     * @return 返回值
     */
    @ExceptionHandler(MismatchedInputException.class)
    public Result<Void> paramSerialException(MismatchedInputException e, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
        return Result.customize(Result.BAD_REQUEST, "参数序列化错误：" + e.getTargetType().getName(),
                Lu.isPresentTracing() ? Lu.basic.tracing.getTraceId() : null);
    }

    /**
     * 业务异常处理，打印warn级别日志，默认不告警
     *
     * @param e
     * @param response
     * @return 返回值
     */
    @ExceptionHandler(LuminousBizException.class)
    public Result<Void> bizException(LuminousBizException e, HttpServletResponse response) {
        log.warn("errCode={}, 业务处理失败:{}", e.getCode(), e.getMessage(), e);
        response.setContentType("application/json;charset=UTF-8");
        String errMsg = e.getMessage();
        String code;
        try {
            if (StringUtils.hasLength(e.getCode())) {
                code = e.getCode();
            } else {
                code = Result.BAD_REQUEST;
            }
        } catch (Exception ex) {
            code = Result.BAD_REQUEST;
        }
        return Result.customize(code, errMsg, Lu.isPresentTracing() ? Lu.basic.tracing.getTraceId() : null);
    }

    /**
     * 未知运行时异常，打印error日志，强制告警
     *
     * @param e
     * @param response
     * @return 返回值
     * @throws IOException
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> exception(Exception e, HttpServletResponse response) {
        log.error("errCode=" + ExceptionCode.UN_KNOW_ERROR + ", 系统异常:", e);
        response.setContentType("application/json;charset=UTF-8");
        String errMsg = "服务暂时不可用，请稍后重试！";
        if (exceptionDevelopMode) {
            errMsg = parseExceptionTrace(e);
        } else {
            if (!ObjectUtil.isEmpty(e.getMessage())) {
                errMsg = e.getMessage();
            }
        }

        return Result.customize(Result.SERVER_ERROR, errMsg, Lu.isPresentTracing() ? Lu.basic.tracing.getTraceId() : null);
    }

    @ExceptionHandler(ClientAbortException.class)
    public void clientAbortException(ClientAbortException e) {
        log.warn("Request Reset Error: ", e);
    }

    public String parseExceptionTrace(Exception e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("处理失败，异常信息：").append(e.getMessage());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            stringBuilder.append("\n   ").append(stackTraceElement.toString());
        }
        return stringBuilder.toString();
    }
}
