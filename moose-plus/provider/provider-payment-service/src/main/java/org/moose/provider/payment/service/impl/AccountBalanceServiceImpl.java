package org.moose.provider.payment.service.impl;

import java.math.BigDecimal;
import javax.annotation.Resource;
import org.apache.dubbo.config.annotation.Service;
import org.moose.commons.base.dto.ResultCode;
import org.moose.commons.base.snowflake.SnowflakeIdWorker;
import org.moose.commons.provider.exception.ProviderRpcException;
import org.moose.provider.payment.mapper.AccountBalanceMapper;
import org.moose.provider.payment.model.domain.AccountBalanceDO;
import org.moose.provider.payment.model.dto.AccountBalanceDTO;
import org.moose.provider.payment.service.AccountBalanceService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2020 2020/4/11 11:59
 * @see org.moose.provider.payment.service.impl
 */
@Component
@Service(version = "1.0.0")
public class AccountBalanceServiceImpl implements AccountBalanceService {

  @Resource
  private AccountBalanceMapper accountBalanceMapper;

  @Resource
  private SnowflakeIdWorker snowflakeIdWorker;

  @Override public AccountBalanceDTO getBalanceByAccountId(Long accountId) {
    if (accountId == null) {
      throw new ProviderRpcException(ResultCode.PAYMENT_ACCOUNT_NOT_FOUND);
    }

    AccountBalanceDO balanceDO = accountBalanceMapper.findByAccountId(accountId);
    if (balanceDO == null) {
      return null;
    }

    AccountBalanceDTO balanceDTO = new AccountBalanceDTO();
    BeanUtils.copyProperties(balanceDO, balanceDTO);
    return balanceDTO;
  }

  /**
   * 充值余额
   * @param accountBalanceDTO AccountBalanceDTO
   */
  @Transactional(rollbackFor = {Exception.class})
  @Override
  public void increaseAccountBalance(AccountBalanceDTO accountBalanceDTO) {
    // 检查传递的 AccountBalance
    if (accountBalanceDTO == null) {
      throw new ProviderRpcException(ResultCode.PAYMENT_ACCOUNT_BALANCE_MUST_NOT_BE_NULL);
    }

    // 账户第一次充值，不存在该用户，添加该用户信息
    AccountBalanceDTO balanceDTO = this.getBalanceByAccountId(accountBalanceDTO.getAccountId());
    if (balanceDTO == null) {
      AccountBalanceDO accountBalanceDO = new AccountBalanceDO();
      accountBalanceDO.setId(snowflakeIdWorker.nextId());
      BeanUtils.copyProperties(accountBalanceDTO, accountBalanceDO);
      int result = accountBalanceMapper.operatorAccountBalance(accountBalanceDO);
      if (result < 1) {
        throw new ProviderRpcException(ResultCode.PAYMENT_INCREASE_ACCOUNT_BALANCE_FAIL);
      }
      return;
    }

    // 不是第一次，更新
    AccountBalanceDO accountBalanceDO = new AccountBalanceDO();
    BeanUtils.copyProperties(accountBalanceDTO, accountBalanceDO);
    accountBalanceDO.setBalance(accountBalanceDTO.getBalance().multiply(new BigDecimal(1)));
    int result = accountBalanceMapper.updateBalanceByAccountId(accountBalanceDO);
    if (result < 1) {
      throw new ProviderRpcException(ResultCode.PAYMENT_INCREASE_ACCOUNT_BALANCE_FAIL);
    }
  }

  /**
   * 扣减余额
   *
   * @param accountBalanceDTO AccountBalanceDTO
   */
  @Transactional(rollbackFor = {Exception.class})
  @Override
  public void reduceAccountBalance(AccountBalanceDTO accountBalanceDTO) {
    if (accountBalanceDTO == null) {
      throw new ProviderRpcException(ResultCode.PAYMENT_ACCOUNT_BALANCE_MUST_NOT_BE_NULL);
    }

    AccountBalanceDTO balanceDTO = this.getBalanceByAccountId(accountBalanceDTO.getAccountId());
    if (balanceDTO == null) {
      throw new ProviderRpcException(ResultCode.PAYMENT_ACCOUNT_NOT_FOUND);
    }

    if (balanceDTO.getBalance().compareTo(accountBalanceDTO.getBalance()) == -1) {
      throw new ProviderRpcException(ResultCode.PAYMENT_BALANCE_NOT_ENOUGH);
    }

    AccountBalanceDO accountBalanceDO = new AccountBalanceDO();
    BeanUtils.copyProperties(accountBalanceDTO, accountBalanceDO);
    accountBalanceDO.setBalance(accountBalanceDTO.getBalance().multiply(new BigDecimal(-1)));
    int result = accountBalanceMapper.updateBalanceByAccountId(accountBalanceDO);
    if (result < 1) {
      throw new ProviderRpcException(ResultCode.PAYMENT_REDUCE_ACCOUNT_BALANCE_FAIL);
    }
  }
}
