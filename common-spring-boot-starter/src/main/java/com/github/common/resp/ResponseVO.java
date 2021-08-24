package com.github.common.resp;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.util.I18nMessageUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author chenjianhua
 * @date 2020-09-01 16:46:26
 */
@Getter
@Setter
public class ResponseVO<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 返回状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String msg;

    /**
     * 返回内容
     */
    private T data;

    public ResponseVO() {

    }

    public ResponseVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseVO(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess() {
        if (ResponseStatusEnum.SUCCESS.getErrorCode().equals(this.code)) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    public boolean isSuccessDataNotNull() {
        if (ResponseStatusEnum.SUCCESS.getErrorCode().equals(this.code)
                && null != this.data) {
            return true;
        }
        return false;
    }

    /**
     * 快速返回成功
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseVO ok() {
        return new ResponseVO<T>(ResponseStatusEnum.SUCCESS.getErrorCode(),
                I18nMessageUtil.getMessage(ResponseStatusEnum.SUCCESS.getI18nKey(), null));
    }

    public static <T> ResponseVO ok(T result) {
        return new ResponseVO<>(ResponseStatusEnum.SUCCESS.getErrorCode(),
                I18nMessageUtil.getMessage(ResponseStatusEnum.SUCCESS.getI18nKey(), null), result);
    }

    public static <T> ResponseVO ok(String message, T result) {
        return new ResponseVO<>(ResponseStatusEnum.SUCCESS.getErrorCode(),
                I18nMessageUtil.getMessage(message, null),
                result);
    }

    /**
     * 快速返回失败状态
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseVO fail() {
        return fail(ResponseStatusEnum.REQUEST_ERROR);
    }

    public static <T> ResponseVO fail(ResponseStatusEnum responseStatusEnum) {
        return new ResponseVO<>(responseStatusEnum.getErrorCode(),
                I18nMessageUtil.getMessage(responseStatusEnum.getI18nKey(), null));
    }

    public static <T> ResponseVO fail(String message) {
        return fail(message, null);
    }

    public static <T> ResponseVO fail(String message, T result) {
        return new ResponseVO<>(ResponseStatusEnum.REQUEST_ERROR.getErrorCode(),
                message,
                result);
    }

    /**
     * org.springframework.http.HttpStatus
     * 快速返回Http状态
     */
    public static <T> ResponseVO httpStatus(HttpStatus httpStatus, String message) {
        return new ResponseVO<T>(httpStatus.value(),
                I18nMessageUtil.getMessage(message, null));
    }

    public static <T> ResponseVO httpStatus(HttpStatus httpStatus, String message, T result) {
        return new ResponseVO<>(httpStatus.value(),
                I18nMessageUtil.getMessage(message, null),
                result);
    }

    @Override
    public String toString() {
        return JsonUtil.toJsonString(this);
    }
}