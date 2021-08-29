package com.github.chenjianhua.common.excel.controller;

import com.github.chenjianhua.common.excel.entity.importexcel.ImportTaskParam;
import com.github.chenjianhua.common.excel.support.ImportTaskManager;
import com.github.chenjianhua.common.excel.entity.importexcel.ImportResultVo;
import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.config.exception.BusinessException;
import com.github.common.resp.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenjianhua
 * @date 2021/8/25
 */
@Slf4j
@Controller
@RequestMapping("/importExcel")
public class ImportExcelController {
    private static final String IMPORT_TYPE = "importType";

    private static final String UPLOAD_FILE = "file";

    @ResponseBody
    @PostMapping("/async")
    public ResponseVO<ImportResultVo> asyncImport(@RequestHeader(value = "auth_token") String authToken,
                                                  @RequestParam(UPLOAD_FILE) MultipartFile file,
                                                  HttpServletRequest request) {

        checkExcel(file);
        ImportTaskParam importTaskParam = buildImportTaskMeta(request, authToken);
        log.info("asyncImport收到导入请求:{}", JsonUtil.toJsonString(importTaskParam));
        importTaskParam.setSyncTask(false);
        importTaskParam.setFile(file);
        ResponseVO<ImportResultVo> importResp = ImportTaskManager.excelImport(importTaskParam);
        log.info("导入结果:{}", JsonUtil.toJsonString(importResp));
        return importResp;
    }

    @ResponseBody
    @PostMapping("/sync")
    public ResponseVO<ImportResultVo> syncImport(@RequestHeader(value = "auth_token") String authToken,
                                                 @RequestParam("file") MultipartFile file,
                                                 HttpServletRequest request) {
        checkExcel(file);
        ImportTaskParam importTaskParam = buildImportTaskMeta(request, authToken);
        log.info("asyncImport收到导入请求:{}", JsonUtil.toJsonString(importTaskParam));
        importTaskParam.setSyncTask(true);
        importTaskParam.setFile(file);
        ResponseVO<ImportResultVo> importResp = ImportTaskManager.excelImport(importTaskParam);
        log.info("导入结果:{}", JsonUtil.toJsonString(importResp));
        return importResp;
    }


    private void checkExcel(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (!StringUtils.hasLength(fileName) || 0 == file.getSize()) {
            throw new BusinessException("上传文件为空");
        }
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!"xlsx".equals(fileSuffix) && !"xls".equals(fileSuffix)) {
            throw new BusinessException("文件格式错误");
        }
    }

    private ImportTaskParam buildImportTaskMeta(HttpServletRequest request, String authToken) {
        String importType = request.getParameter(IMPORT_TYPE);
        if (!StringUtils.hasText(importType)) {
            throw new BusinessException("importType导入类型不能为空");
        }
        Map<String, String[]> paramMap = request.getParameterMap();
        ImportTaskParam importTaskParam = new ImportTaskParam();
        importTaskParam.setImportCode(importType);
        // 导入参数
        Map<String, Object> importArgs = new HashMap<>(8);
        paramMap.entrySet().forEach(item -> {
            if (null != item.getValue() && !IMPORT_TYPE.equals(item.getKey()) && item.getValue().length >= 1) {
                importArgs.put(item.getKey(), item.getValue()[0]);
            }
        });
        importTaskParam.setImportArg(importArgs);
        importTaskParam.setAuthToken(authToken);
        return importTaskParam;
    }
}
