package com.github.chenjianhua.common.excel.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Getter
@Setter
@ToString
public class FileUploadResponse {

    private Integer code = 200;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 文件存放地址
     */
    private String url;
    /**
     * 上传文件大小
     */
    private Long fileSize;

    public FileUploadResponse(){}

    public FileUploadResponse(Integer code, String msg, String url){
        this.code = code;
        this.msg = msg;
        this.url = url;
    }

    public static FileUploadResponse ok(String url){
        return new FileUploadResponse(200, null, url);
    }

    public static FileUploadResponse error(String msg, String url){
        return new FileUploadResponse(500, msg, url);
    }
}

