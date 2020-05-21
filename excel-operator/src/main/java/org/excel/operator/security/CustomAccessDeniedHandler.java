package org.excel.operator.security;

import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.excel.operator.common.api.ResponseResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/11/20 22:54
 * @see org.excel.operator.security
 */
@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException e) throws
      IOException {
    log.info(" >>>> CustomAccessDeniedHandler >>>> 用户无权访问");
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_OK);
    PrintWriter writer = response.getWriter();
    writer.write(JSON.toJSONString(ResponseResult.fail(e.getMessage())));
    writer.close();
  }
}
