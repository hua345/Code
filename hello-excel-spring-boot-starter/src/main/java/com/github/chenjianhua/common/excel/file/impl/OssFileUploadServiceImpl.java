package com.github.chenjianhua.common.excel.file.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.github.chenjianhua.common.excel.bo.FileUploadResponse;
import com.github.chenjianhua.common.excel.config.OosConfig;
import com.github.chenjianhua.common.excel.file.FileUploadService;
import com.github.chenjianhua.common.excel.util.UuidUtil;
import com.github.chenjianhua.common.json.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;

/**
 * @author chenjianhua
 * @date 2021/8/25
 */
@Slf4j
@Service
public class OssFileUploadServiceImpl implements FileUploadService {
    private static OosConfig oosConfig = new OosConfig();

    /**
     * @param prefixFolder 上传目录（可为空）
     * @param fileName     文件名
     * @param fileStream   上传文件输入流
     */
    @Override
    public FileUploadResponse uploadFile(String prefixFolder, String fileName, InputStream fileStream) {
        return upload(prefixFolder, fileName, fileStream, null);
    }

    /**
     * @param prefixFolder 上传目录（可为空）
     * @param fileName     文件名
     * @param file         上传文件
     */
    @Override
    public FileUploadResponse uploadFile(String prefixFolder, String fileName, File file) {
        return upload(prefixFolder, fileName, null, file);
    }

    private static FileUploadResponse upload(String prefixFolder, String fileName, InputStream fileStream, File file) {
        if(!StringUtils.hasText(prefixFolder)){
            prefixFolder = "resource/";
        }
        if(!StringUtils.hasText(fileName)){
            prefixFolder.concat(System.currentTimeMillis() + "");
        }
        log.info("传输文件{}至oss {}开始", fileName, prefixFolder);
        FileUploadResponse uploadResponse = new FileUploadResponse();
        long start = System.currentTimeMillis();
        OSSClient ossClient = new OSSClient(oosConfig.getEndpoint(), oosConfig.getAccessKeyId(), oosConfig.getAccessKeySecret());
        String url = null;
        try {
            String bucketName = oosConfig.getBucketName();
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
            }

            String fileKey = prefixFolder + fileName;

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentEncoding("utf-8");
            if(fileStream != null) {
                meta.setContentLength(fileStream.available());
                ossClient.putObject(bucketName, fileKey, fileStream, meta);
            }else {
                ossClient.putObject(bucketName, fileKey, file, meta);
                uploadResponse.setFileSize(file.length());
            }

            url = "https://" + bucketName + "." + oosConfig.getEndpoint() + "/" + fileKey;
        } catch (OSSException oe) {
            log.error("OSSException异常：", oe);
            uploadResponse = new FileUploadResponse(500, "OOS异常", null);
        } catch (ClientException ce) {
            log.error("ClientException异常：", ce);
            uploadResponse = new FileUploadResponse(500, "OOS-CLIENT异常", null);
        } catch (Exception e) {
            log.error("上传文件异常:", e);
            uploadResponse = new FileUploadResponse(500, "上传文件异常", null);
        } finally {
            ossClient.shutdown();
        }
        uploadResponse.setUrl(url);
        log.info("传输文件{}至oss {}完成{}ms, url:{}",fileName , prefixFolder, System.currentTimeMillis() - start, url);
        return uploadResponse;
    }
}
