package org.moose.business.user.service.impl;

import com.google.common.collect.Maps;
import java.util.Date;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.dubbo.config.annotation.Reference;
import org.moose.business.oauth.feign.OAuth2RequestTokenApi;
import org.moose.business.user.constants.OAuth2Constants;
import org.moose.business.user.model.params.LoginParam;
import org.moose.business.user.model.params.RegisterParam;
import org.moose.business.user.service.UserService;
import org.moose.commons.base.dto.ResponseResult;
import org.moose.commons.base.dto.ResultCode;
import org.moose.commons.base.snowflake.SnowflakeIdWorker;
import org.moose.provider.account.model.dto.AccountDTO;
import org.moose.provider.account.model.dto.PasswordDTO;
import org.moose.provider.account.model.dto.RoleDTO;
import org.moose.provider.account.service.AccountService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2020-03-24 13:31:13:31
 * @see org.moose.business.user.service.impl
 */
@Component
public class UserServiceImpl implements UserService {

  @Resource
  private BCryptPasswordEncoder passwordEncoder;

  @Resource
  private SnowflakeIdWorker snowflakeIdWorker;

  @Reference(version = "1.0.0")
  private AccountService accountService;

  @Resource
  private OAuth2RequestTokenApi oAuth2RequestTokenApi;

  @Override public ResponseResult<?> login(LoginParam loginParam) {
    Map<String, String> param = Maps.newHashMap();
    param.put("username", loginParam.getAccountName());
    param.put("password", loginParam.getPassword());
    param.put("grant_type", OAuth2Constants.OAUTH2_GRANT_TYPE);
    param.put("client_id", OAuth2Constants.OAUTH2_CLIENT_ID);
    param.put("client_secret", OAuth2Constants.OAUTH2_CLIENT_SECRET);

    Map<String, Object> authToken = oAuth2RequestTokenApi.getOAuthToken(param);
    if (authToken == null) {
      return new ResponseResult<Map<String, Object>>(ResultCode.NET_WORK_UNKNOWN.getCode(),
          ResultCode.NET_WORK_UNKNOWN.getMessage());
    }

    String accessToken = (String) authToken.get("access_token");
    if (accessToken == null) {
      Integer code = (Integer) authToken.get("code");
      String message = (String) authToken.get("message");
      return new ResponseResult<Map<String, Object>>(code, message);
    }

    String refreshToken = (String) authToken.get("refresh_token");
    Map<String, Object> result = Maps.newHashMap();
    result.put("accessToken", accessToken);
    result.put("refreshToken", refreshToken);
    return new ResponseResult<>(result);
  }

  @Override public ResponseResult<?> register(RegisterParam registerParam) {

    Long accountId = snowflakeIdWorker.nextId();
    AccountDTO accountDTO = new AccountDTO();
    accountDTO.setAccountId(accountId);
    accountDTO.setAccountName(registerParam.getAccountName());
    accountDTO.setPhone(registerParam.getPhone());
    accountDTO.setCreateTime(new Date());
    accountDTO.setUpdateTime(new Date());

    Long passwordId = snowflakeIdWorker.nextId();
    PasswordDTO passwordDTO = new PasswordDTO();
    passwordDTO.setAccountId(accountId);
    passwordDTO.setPassword(passwordEncoder.encode(registerParam.getPassword()));
    passwordDTO.setPasswordId(passwordId);
    passwordDTO.setCreateTime(new Date());
    passwordDTO.setUpdateTime(new Date());

    Long roleId = snowflakeIdWorker.nextId();
    RoleDTO roleDTO = new RoleDTO();
    roleDTO.setRoleId(roleId);
    roleDTO.setRole("USER");
    roleDTO.setAccountId(accountId);
    roleDTO.setCreateTime(new Date());
    roleDTO.setUpdateTime(new Date());

    boolean result = accountService.add(accountDTO, passwordDTO, roleDTO);
    return new ResponseResult<>(result);
  }

  @Override public ResponseResult<?> logout(String accessToken) {
    boolean result = oAuth2RequestTokenApi.deleteToken(accessToken);
    return new ResponseResult<>(result);
  }
}
