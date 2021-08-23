package com.github.chenjianhua.common.excel.util;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.github.chenjianhua.common.excel.bo.ept.ExportedMeta;
import com.github.chenjianhua.common.excel.bo.ipt.ImportTaskMeta;
import com.github.chenjianhua.common.excel.enums.ExcelConstants;
import com.szkunton.common.ktcommon.exception.BusinessException;
import com.github.chenjianhua.common.excel.bo.FileUploadResponse;
import com.github.chenjianhua.common.excel.support.UploadHandler;
import com.szkunton.common.ktjson.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author chenjianhua
 * @date 2021/3/30
 */
@Slf4j
public class ExcelUploadUtil {
    /**
     * 上传的导入文件保存到本地temp文件和上传到oss
     */
    public static void handleUploadFile(ImportTaskMeta taskMeta) throws IOException {
        // 将上传的文件流保存到本地
        File uploadOriginFile = ExcelUploadUtil.uploadFileToTempFile(taskMeta.getFile());
        // 上传导入原始文件到oss
        FileUploadResponse fileUploadResponse = ExcelUploadUtil.uploadImport(uploadOriginFile);
        log.info("导入[{}]原文件上传结果{}", taskMeta.getTaskNumber(), JsonUtils.toJSONString(fileUploadResponse));
        // 检查导出文件上传状态
        if (null == fileUploadResponse || ExcelConstants.RESP_SUCCESS_STATUS != fileUploadResponse.getCode()) {
            throw new BusinessException("上传导入原文件异常");
        }
        taskMeta.setImportOssFilePath(fileUploadResponse.getUrl());
        taskMeta.setUploadOriginTempFile(uploadOriginFile);
    }

    /**
     * 将上传的文件流保存到本地
     */
    public static File uploadFileToTempFile(MultipartFile file) throws IOException {
        InputStream in = file.getInputStream();
        int len = 0;
        byte buffer[] = new byte[1024];
        File originTempFile = Files.createTempFile(ExcelConstants.IMPORT_ORIGIN_NAME + UuidUtil.getUuid32(), ExcelTypeEnum.XLSX.getValue()).toFile();
        FileOutputStream out = new FileOutputStream(originTempFile);
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        in.close();
        out.close();
        return originTempFile;
    }

    public static FileUploadResponse uploadExport(ExportedMeta exportedMeta) {
        File exportFile = exportedMeta.getExportFileMeta().getExportFile();
        return UploadHandler.upload("excel-export/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "/", exportFile.getName(), exportFile);
    }

    public static FileUploadResponse uploadImport(File file) throws IOException {
        return UploadHandler.upload("excel-import/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "/", file.getName(), file);
    }
}
