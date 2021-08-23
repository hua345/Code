package com.github.common.config.exception;

import com.github.common.resp.ResponseStatusEnum;
import com.github.common.resp.ResponseVO;
import lombok.Getter;

/**
 * @author chenjianhua
 * @date 2020-09-07 15:41:49
 */
@Getter
public class BusinessException extends RuntimeException {

    private Integer code;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ResponseStatusEnum responseStatusEnum) {
        super(responseStatusEnum.getI18nKey());
        this.code = responseStatusEnum.getErrorCode();
    }

    public BusinessException(String message) {
        super(message);
        this.code = ResponseStatusEnum.SUCCESS.getErrorCode();
    }

    public ResponseVO getResponseResult() {
        return new ResponseVO(this.code, this.getMessage());
    }
}