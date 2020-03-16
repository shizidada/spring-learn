package org.moose.account.service.impl;

import javax.annotation.Resource;
import org.apache.dubbo.config.annotation.Service;
import org.moose.account.mapper.PasswordMapper;
import org.moose.account.model.domain.PasswordDO;
import org.moose.account.model.dto.PasswordDTO;
import org.moose.account.service.PasswordService;
import org.springframework.beans.BeanUtils;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2020 2020/3/16 23:40
 * @see org.moose.account.service.impl
 */
@Service(version = "1.0.0")
public class PasswordServiceImpl implements PasswordService {

  @Resource
  private PasswordMapper passwordMapper;

  @Override
  public PasswordDTO get(String accountId) {
    PasswordDO passwordDO = passwordMapper.findByAccountId(accountId);
    PasswordDTO passwordDTO = new PasswordDTO();
    BeanUtils.copyProperties(passwordDO, passwordDTO);
    return passwordDTO;
  }
}