package com.github.chenjianhua.common.excel.file;

import com.github.chenjianhua.common.excel.entity.FileUploadResponse;

import java.io.File;
import java.io.InputStream;

/**
 * @author chenjianhua
 * @date 2021/8/25
 */
public interface FileUploadService {
    /**
     * @param prefixFolder 上传目录（可为空）
     * @param fileName     文件名
     * @param fileStream   上传文件输入流
     */
    FileUploadResponse uploadFile(String prefixFolder, String fileName, InputStream fileStream);

    /**
     * @param prefixFolder 上传目录（可为空）
     * @param fileName     文件名
     * @param file         上传文件
     */
    FileUploadResponse uploadFile(String prefixFolder, String fileName, File file);
}
