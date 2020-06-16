package org.excel.operator.web.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.excel.operator.common.api.ResultCode;
import org.excel.operator.constants.SecurityConstants;
import org.excel.operator.exception.BusinessException;
import org.excel.operator.web.security.sms.ValidateCode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2020-06-15 22:04:22:04
 * @see org.excel.operator.web.security.sms
 */
public class SmsCodeFilter extends OncePerRequestFilter implements InitializingBean {

  private static final String SMS_CODE_FILTER_URL = "/authentication/mobile";

  private static final String SMS_CODE_POST_METHOD = "POST";

  private static final String SMS_CODE_GET_METHOD = "get";

  /**
   * 存放所有需要校验验证码的url
   */
  private Map<String, String> urlMap = new HashMap<>();
  /**
   * 验证请求url与配置的url是否匹配的工具类
   */
  private AntPathMatcher pathMatcher = new AntPathMatcher();

  private AuthenticationFailureHandler authenticationFailureHandler;

  public SmsCodeFilter() {
  }

  public SmsCodeFilter(
      AuthenticationFailureHandler authenticationFailureHandler) {
    this.authenticationFailureHandler = authenticationFailureHandler;
  }

  /**
   * 初始化要拦截的url配置信息
   */
  @Override
  public void afterPropertiesSet() throws ServletException {
    super.afterPropertiesSet();
    addUrlToMap(SMS_CODE_FILTER_URL, SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_SMS);
  }

  /**
   * 讲系统中配置的需要校验验证码的URL根据校验的类型放入map
   *
   * @param urlString
   * @param smsParam
   */
  protected void addUrlToMap(String urlString, String smsParam) {
    if (StringUtils.isNotBlank(urlString)) {
      String[] urls = StringUtils.splitByWholeSeparatorPreserveAllTokens(urlString, ",");
      for (String url : urls) {
        urlMap.put(url, smsParam);
      }
    }
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (StringUtils.equals(request.getRequestURI(), SMS_CODE_FILTER_URL)
        && StringUtils.equalsIgnoreCase(
        request.getMethod(), SMS_CODE_POST_METHOD)) {
      try {
        validate(request);
      } catch (BusinessException e) {
        authenticationFailureHandler.onAuthenticationFailure(request, response, e);
        return;
      }
    }

    /**
     * 如果不是登录请求，直接调用后面的过滤器链
     */
    filterChain.doFilter(request, response);
  }

  /**
   * 获取校验码
   *
   * @param request
   * @return
   */
  private String getValidateCode(HttpServletRequest request) {
    String result = null;
    if (!StringUtils.equalsIgnoreCase(request.getMethod(), SMS_CODE_GET_METHOD)) {
      Set<String> urls = urlMap.keySet();
      for (String url : urls) {
        if (pathMatcher.match(url, request.getRequestURI())) {
          result = urlMap.get(url);
        }
      }
    }
    return result;
  }

  private void validate(HttpServletRequest request) {
    String smsCode = request.getParameter("smsCode");
    if (StringUtils.isBlank(smsCode)) {
      throw new BusinessException(ResultCode.SMS_CODE_IS_EMPTY);
    }
    HttpSession session = request.getSession();
    ValidateCode validateCode = (ValidateCode) session.getAttribute("SMS_SESSION_KEY");
    if (validateCode == null) {
      throw new BusinessException(ResultCode.SMS_CODE_NOT_EXITS);
    }
    if (validateCode.isExpried()) {
      session.removeAttribute("SMS_SESSION_KEY");
      throw new BusinessException(ResultCode.SMS_CODE_IS_EXPRIED);
    }
    if (!validateCode.getCode().equals(smsCode)) {
      throw new BusinessException(ResultCode.SMS_CODE_ERROR);
    }
    session.removeAttribute("SMS_SESSION_KEY");
  }
}
