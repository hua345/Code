package com.github.common.config.exception;


import com.github.chenjianhua.common.json.util.JsonUtil;
import com.github.common.resp.ResponseStatusEnum;
import com.github.common.resp.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenjianhua
 * @date 2020-09-07 15:41:49
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseVO defaultErrorHandler(Exception e) {
        log.error("Exception", e);
        return ResponseVO.fail(ResponseStatusEnum.SERVER_ERROR);
    }

    /**
     * GET/POST请求方法错误的拦截器
     * 因为开发时可能比较常见,而且发生在进入controller之前,上面的拦截器拦截不到这个错误
     * 所以定义了这个拦截器
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseVO httpRequestMethodHandler() {
        log.error("Catch HttpRequestMethodNotSupportedException");
        return ResponseVO.fail(ResponseStatusEnum.REQUEST_METHOD_ERROR);
    }

    /**
     * 使用@RequestParam时，请求参数缺失时抛出的异常
     * //@GetMapping(path = "/")
     * public ResponseVO<GetUserOutputDTO> getUser(@RequestParam String name)
     *
     * @param missingServletRequestParameterException MissingServletRequestParameterException
     * @return 全局统一返回体
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseVO requestParameterExceptionHandler(MissingServletRequestParameterException missingServletRequestParameterException) {
        log.error("Catch MissingServletRequestParameterException {}.", missingServletRequestParameterException.getMessage());
        return new ResponseVO(ResponseStatusEnum.PARAMETER_CHECK_ERROR.getErrorCode(), missingServletRequestParameterException.getMessage());
    }

    /**
     * 使用@RequestHeader时，请求参数缺失时抛出的异常
     *
     * @param missingRequestHeaderException MissingRequestHeaderException
     * @return 全局统一返回体
     * @RequestHeader(value = "auth_token") String authToken
     */
    @ExceptionHandler({MissingRequestHeaderException.class})
    public ResponseVO missingRequestHeaderExceptionHandler(MissingRequestHeaderException missingRequestHeaderException) {
        log.error("Catch MissingRequestHeaderException {}.", missingRequestHeaderException.getMessage());
        return new ResponseVO(ResponseStatusEnum.PARAMETER_CHECK_ERROR.getErrorCode(), missingRequestHeaderException.getMessage());
    }

    /**
     * 所有业务异常统一处理入口 （默认 HttpStatus.OK = 200）
     *
     * @param businessException 业务异常
     * @return 全局统一返回体
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseVO myRuntimeExceptionHandler(BusinessException businessException) {
        log.error("Catch {} MyRuntimeException ; {}", businessException.getStackTrace()[0].toString(), businessException.getResponseResult());
        return businessException.getResponseResult();
    }

    /**
     * 处理Get请求中 使用@Valid 验证路径中请求实体校验失败后抛出的异常
     * 参数校验不通过异常处理
     * //@GetMapping(path = "/")
     * public ResponseVO<GetUserOutputDTO> getUser(@Valid/@Validated GetUserInputDTO param)
     *
     * @param e validation 校验异常
     * @return 返回给前台的响应实体，会被Jackson序列化成json
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseVO bindExceptionHandler(BindException e) {
        log.info("BindException Handler--- ERROR: {}", JsonUtil.toJsonString(e.getBindingResult().getAllErrors()));
        String message = e.getBindingResult().getAllErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining());
        ResponseVO<String> response = ResponseVO.fail(ResponseStatusEnum.PARAMETER_CHECK_ERROR);
        response.setData(message);
        return response;
    }

    /**
     * 使用@RequestParam上validate失败后抛出的异常是javax.validation.ConstraintViolationException
     * 处理请求参数格式错误
     * //@Validated
     * public class UserController {
     * //   @GetMapping(path = "/")
     * public ResponseVO<GetUserOutputDTO> getUser(@NotBlank(message = "名字不能为空") @RequestParam String name)
     * }
     *
     * @param e ConstraintViolationException
     * @return ResponseVO
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseVO constraintViolationExceptionHandler(ConstraintViolationException e) {
        log.info("ConstraintViolationException Handler--- ERROR: {}", e.getConstraintViolations());
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage).collect(Collectors.joining());
        ResponseVO<String> response = ResponseVO.fail(ResponseStatusEnum.PARAMETER_CHECK_ERROR);
        response.setData(message);
        return response;
    }

    /**
     * 使用@Validated @RequestBody上校验参数失败后抛出的异常是MethodArgumentNotValidException异常。
     * org.springframework.validation.annotation.Validated
     * //@PostMapping(path = "/")
     * public ResponseVO<GetUserOutputDTO> addUser(@Valid/@Validated @RequestBody GetUserInputDTO param)
     *
     * @param e MethodArgumentNotValidException 校验异常
     * @return 返回给前台的响应实体，会被Jackson序列化成json
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVO methodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        StringBuffer errorMsg = new StringBuffer();
        errors.forEach(x -> errorMsg.append(x.getDefaultMessage()).append(";"));
        log.error("MethodArgumentNotValidException Handler--- ERROR: {}", errorMsg.toString());
        ResponseVO<String> response = ResponseVO.fail(ResponseStatusEnum.PARAMETER_CHECK_ERROR);
        response.setData(errorMsg.toString());
        return response;
    }
}

