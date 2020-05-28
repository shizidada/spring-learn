package org.excel.operator.web.service.impl;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.excel.operator.common.api.ResultCode;
import org.excel.operator.exception.BusinessException;
import org.excel.operator.web.security.CustomUserDetails;
import org.excel.operator.web.service.model.AccountModel;
import org.excel.operator.web.service.model.PasswordModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/11/20 21:22
 * @see org.excel.operator.web.service.impl
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Resource
  private AccountServiceImpl accountService;

  @Resource
  private PasswordServiceImpl passwordService;

  @Override public UserDetails loadUserByUsername(String accountName)
      throws UsernameNotFoundException {
    if (StringUtils.isEmpty(accountName)) {
      throw new BusinessException(ResultCode.ACCOUNT_NOT_EMPTY);
    }

    AccountModel accountModel = accountService.getAccountByAccountName(accountName);
    if (accountModel == null) {
      throw new BusinessException(ResultCode.ACCOUNT_OR_PASSWORD_ERROR);
    }

    PasswordModel passwordModel = passwordService.findByAccountId(accountModel.getAccountId());
    if (passwordModel == null) {
      throw new BusinessException(ResultCode.ACCOUNT_OR_PASSWORD_ERROR);
    }
    // TODO 角色、权限集合
    List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();
    grantedAuthorities.add(new SimpleGrantedAuthority("USER"));
    return new CustomUserDetails(accountModel, passwordModel, grantedAuthorities);
  }
}
