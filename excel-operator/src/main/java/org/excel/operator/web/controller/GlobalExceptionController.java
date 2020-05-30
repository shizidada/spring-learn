package org.excel.operator.web.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.excel.operator.common.api.ResponseResult;
import org.excel.operator.common.api.ResultCode;
import org.excel.operator.exception.BusinessException;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/11/19 18:55
 * @see org.excel.operator.web.controller
 */
@RestController
@Slf4j
public class GlobalExceptionController extends AbstractErrorController {

  private static final String ERROR_PATH = "/error";

  public GlobalExceptionController(ErrorAttributes errorAttributes) {
    super(errorAttributes);
  }

  @Override public String getErrorPath() {
    return ERROR_PATH;
  }

  private Throwable getError(WebRequest webRequest) {
    return (Throwable) this.getAttribute(webRequest, "javax.servlet.error.exception");
  }

  private Object getAttribute(RequestAttributes requestAttributes, String name) {
    return requestAttributes.getAttribute(name, 0);
  }

  @RequestMapping(value = ERROR_PATH)
  @ResponseStatus(HttpStatus.OK)
  public Object error(HttpServletRequest request) {
    WebRequest webRequest = new ServletWebRequest(request);
    Throwable e = getError(webRequest);
    // 创建一个Map进行封装
    if (e == null) {
      Map<String, Object> errorAttributes = getErrorAttributes(request, false);
      log.error("全局异常捕获 Error Attribute [{}]", errorAttributes);
      Integer status = (Integer) errorAttributes.get("status");
      String error = (String) errorAttributes.get("error");
      // 将 map 封装后的错误信息传入，统一返回
      return new ResponseResult<>(status, error);
    }
    log.error("全局异常捕获 [{}]", e.getMessage());
    if (e.getCause() instanceof BusinessException) {
      BusinessException be = (BusinessException) e.getCause();
      return new ResponseResult<>(be.getCode(), be.getMessage());
    } else {
      // 从枚举类中取出自定义的错误码和错误信息
      return new ResponseResult<>(ResultCode.UN_KNOWN_ERROR);
    }
  }

  //@ExceptionHandler(Exception.class)
  //@ResponseStatus(HttpStatus.OK)
  //@ResponseBody
  //public Object handleException(HttpServletRequest request, Exception ex) {
  //
  //  log.error("全局异常捕获. ", ex);
  //
  //  //创建一个Map进行封装
  //  Map<String, Object> responseData = new HashMap<>(16);
  //
  //  //如果捕获到的异常是我们抛出的异常对象
  //  if (ex instanceof BusinessException) {
  //    //强转为自定义类型
  //    BusinessException businessException = (BusinessException) ex;
  //
  //    responseData.put("errCode", businessException.getCode());
  //    responseData.put("errMsg", businessException.getMessage());
  //  } else {
  //    // 从枚举类中取出自定义的错误码和错误信息
  //    responseData.put("errCode", ResultCode.KNOWN_ERROR.getCode());
  //    responseData.put("errMsg", ex.getMessage());
  //  }
  //
  //  //将map封装后的错误信息传入，统一返回
  //  return ResponseResult.fail(responseData);
  //}
}
