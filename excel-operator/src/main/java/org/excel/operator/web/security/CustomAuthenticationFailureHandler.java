package org.excel.operator.web.security;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.excel.operator.common.api.ResponseResult;
import org.excel.operator.common.api.ResultCode;
import org.excel.operator.exception.BusinessException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/11/20 21:56
 * @see org.excel.operator.component
 */
@Component
@Slf4j
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

  @Override public void onAuthenticationFailure(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException e)
      throws IOException, ServletException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);
    ServletOutputStream writer = response.getOutputStream();
    Integer code = HttpServletResponse.SC_UNAUTHORIZED;
    String message = e.getMessage();
    if (e instanceof BusinessException) {
      BusinessException be = (BusinessException) e;
      code = be.getCode();
      message = be.getMessage();
    }
    if (e instanceof DisabledException) {
      code = ResultCode.ACCOUNT_DISABLED.getCode();
      message = ResultCode.ACCOUNT_DISABLED.getMessage();
    }
    log.info("CustomAuthenticationFailureHandler 用户登录失败 [{}] [{}]", code, message);
    writer.write(JSON.toJSONString(new ResponseResult<>(code, message)).getBytes());
    writer.close();
  }
}
