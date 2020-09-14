package org.moose.operator.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.moose.operator.common.api.ResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/11/16 22:43
 * @see org.moose.operator.web.interceptor
 */
public class LoginInterceptor implements HandlerInterceptor {

  public static final String ACCOUNT_NAME = "accountName";
  @Resource private ObjectMapper objectMapper;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) {
    HttpSession session = request.getSession();
    if (session.getAttribute(ACCOUNT_NAME) == null) {
      try {
        this.write(response);
        return false;
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
    }
    return true;
  }

  /**
   * 返回未登录
   *
   * @throws IOException
   */
  private void write(ServletResponse servletResponse)
      throws IOException {
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setHeader("Content-Type", "application/json");
    ServletOutputStream out = response.getOutputStream();
    objectMapper.writeValue(out, new ResponseResult<>(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
  }
}