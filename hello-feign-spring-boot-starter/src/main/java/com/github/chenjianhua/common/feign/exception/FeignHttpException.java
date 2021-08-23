package com.github.chenjianhua.common.feign.exception;

/**
 * feign远程调用异常
 */
public class FeignHttpException extends RuntimeException {

    public FeignHttpException(String message) {
        super(message);
    }

    public FeignHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
