package org.moose.business.user.web.service.impl;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.moose.business.oauth.feign.OAuth2RequestTokenApi;
import org.moose.business.user.web.constants.OAuth2Constants;
import org.moose.business.user.web.model.params.LoginParam;
import org.moose.business.user.web.service.LoginService;
import org.moose.commons.base.dto.ResponseResult;
import org.moose.commons.base.dto.ResultCode;
import org.moose.commons.base.exception.BusinessException;
import org.springframework.stereotype.Service;

/**
 *
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2020 2020/4/11 22:03
 * @see org.moose.business.user.web.service.impl
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

  @Resource
  private OAuth2RequestTokenApi oAuth2RequestTokenApi;

  @Override public ResponseResult<?> login(LoginParam loginParam) {

    /**
     * 检查登录信息
     * @see EnumValidator
     * @see EnumValidatorClass#isValid(Object, ConstraintValidatorContext)
     */

    // 封装请求授权参数
    Map<String, String> param = Maps.newHashMap();

    String loginType = loginParam.getLoginType();
    // password type
    if (loginType.equals(OAuth2Constants.OAUTH2_PASSWORD_GRANT_TYPE)) {
      String accountName = loginParam.getAccountName();
      if (StringUtils.isBlank(accountName)) {
        throw new BusinessException(ResultCode.ACCOUNT_MUST_NOT_BE_NULL);
      }

      String password = loginParam.getPassword();
      if (StringUtils.isBlank(password)) {
        throw new BusinessException(ResultCode.PASSWORD_MUST_NOT_BE_NULL);
      }
      param.put("username", accountName);
      param.put("password", password);
      param.put("grant_type", OAuth2Constants.OAUTH2_PASSWORD_GRANT_TYPE);
    }

    // sms_code
    if (loginType.equals(OAuth2Constants.OAUTH2_SMS_GRANT_TYPE)) {
      String phone = loginParam.getPhone();

      // 手机号码
      if (StringUtils.isBlank(phone)) {
        throw new BusinessException(ResultCode.PHONE_MUST_NOT_BE_NULL);
      }

      // 校验短信验证码
      String smsCode = loginParam.getSmsCode();
      if (StringUtils.isBlank(smsCode)) {
        throw new BusinessException(ResultCode.SMS_CODE_MUST_NOT_BE_NULL);
      }

      param.put("phone", phone);
      param.put("smsCode", smsCode);
      param.put("grant_type", OAuth2Constants.OAUTH2_SMS_GRANT_TYPE);
    }

    param.put("client_id", OAuth2Constants.OAUTH2_CLIENT_ID);
    param.put("client_secret", OAuth2Constants.OAUTH2_CLIENT_SECRET);

    Map<String, Object> authInfo = oAuth2RequestTokenApi.getOAuthToken(param);
    if (authInfo == null) {
      throw new BusinessException(ResultCode.OAUTH_ERROR);
    }

    String accessToken = (String) authInfo.get("access_token");
    if (accessToken == null) {
      Integer code = (Integer) authInfo.get("code");
      String message = (String) authInfo.get("message");
      return new ResponseResult<Map<String, Object>>(code, message);
    }

    String refreshToken = (String) authInfo.get("refresh_token");
    Map<String, Object> result = Maps.newHashMap();
    result.put("access_token", accessToken);
    result.put("refresh_token", refreshToken);
    return new ResponseResult<>(result);
  }
}
