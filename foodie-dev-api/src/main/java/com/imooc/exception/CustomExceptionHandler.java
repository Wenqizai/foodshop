package com.imooc.exception;

import com.imooc.utils.IMOOCJSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * @author liangwq
 * @date 2021/1/10
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * 上传文件超过500k, 捕获此异常
     * @param e
     * @return
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public IMOOCJSONResult handlerMaxUploadFile(MaxUploadSizeExceededException e) {
        return IMOOCJSONResult.errorMsg("文件上传大小不能超过500k, 请压缩图片或者降低图片质量后再上传!");
    }
}
