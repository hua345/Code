package com.github.chenjianhua.common.excel.file.impl;

import com.github.chenjianhua.common.excel.bo.FileUploadResponse;
import com.github.chenjianhua.common.excel.config.ExcelAutoProperties;
import com.github.chenjianhua.common.excel.file.FileUploadService;
import com.github.common.config.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @author chenjianhua
 * @date 2021/8/25
 */
@Slf4j
@Service
public class LocalFileUploadServiceImpl implements FileUploadService {
    private String localPath;

    public LocalFileUploadServiceImpl() {
        ExcelAutoProperties excelAutoProperties = new ExcelAutoProperties();
        this.localPath = excelAutoProperties.getLocalPath();
    }

    public LocalFileUploadServiceImpl(ExcelAutoProperties excelAutoProperties) {
        this.localPath = excelAutoProperties.getLocalPath();
    }

    /**
     * @param prefixFolder 上传目录（可为空）
     * @param fileName     文件名
     * @param fileStream   上传文件输入流
     */
    @Override
    public FileUploadResponse uploadFile(String prefixFolder, String fileName, InputStream fileStream) {
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        OutputStream os = null;
        try {
            String filePath = localPath + File.separator + prefixFolder;
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存到本地文件
            File dirFile = new File(filePath);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            fileUploadResponse.setUrl(prefixFolder + File.separator + fileName);
            os = new FileOutputStream(filePath + File.separator + fileName);
            // 开始读取
            while ((len = fileStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (Exception e) {
            log.error("保存文件异常", e);
            throw new BusinessException("保存文件异常");
        } finally {
            // 完毕，关闭所有链接
            try {
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileUploadResponse;
    }

    /**
     * @param prefixFolder 上传目录（可为空）
     * @param fileName     文件名
     * @param file         上传文件
     */
    @Override
    public FileUploadResponse uploadFile(String prefixFolder, String fileName, File file) {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error("没有找到文件", e);
            throw new BusinessException("没有找到文件");
        }
        return uploadFile(prefixFolder, fileName, inputStream);
    }
}
