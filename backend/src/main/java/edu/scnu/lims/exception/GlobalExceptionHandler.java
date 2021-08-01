package edu.scnu.lims.exception;

import edu.scnu.lims.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * @author ZhuangJieYing
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public CommonResult<String> unknownException(RuntimeException e) {
        log.error("发生了异常", e);

        return CommonResult.failed(e.getMessage());
    }

    /**
     * 处理 @Valid 校验错误时抛出的异常
     * @param e 校验错误类
     * @return 封装错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<String> handleValidException(MethodArgumentNotValidException e){
        //日志记录错误信息
        log.error(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
        //将错误信息返回给前台
        return CommonResult.validateFailed(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

}
