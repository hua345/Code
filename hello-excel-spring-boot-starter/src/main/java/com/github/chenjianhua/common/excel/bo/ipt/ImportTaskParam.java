package com.github.chenjianhua.common.excel.bo.ipt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

/**
 * @author chenjianhua
 * @date 2021/3/22
 */
@Setter
@Getter
@ToString
public class ImportTaskParam {
    /**
     * 导入编号
     */
    private String importCode;
    /**
     * 导入任务编号
     */
    private String taskNumber;
    /**
     * 导入参数
     */
    private Map<String, Object> importArg;
    /**
     * 上传的原文件
     */
    @Deprecated
    private MultipartFile file;
    /**
     * 上传的原文件temp文件路径
     */
    private File uploadOriginTempFile;
    /**
     * 上传的原文件oss地址
     */
    private String importOssFilePath;
    /**
     * 是否同步任务
     */
    private boolean syncTask = true;
    /**
     * 登录token
     */
    private String authToken;
}
