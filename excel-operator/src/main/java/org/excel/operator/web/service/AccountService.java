package org.excel.operator.web.service;

import org.excel.operator.web.service.model.AccountModel;
import org.excel.operator.web.service.model.RegisterInfoModel;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/11/17 13:10
 * @see org.excel.operator.web.service
 */
public interface AccountService {

  /**
   * 注册
   *
   * @param registerInfoModel 注册信息
   */
  void register(RegisterInfoModel registerInfoModel);

  /**
   * 更具账号查找用户
   *
   * @param accountName 账号
   *
   * @return AccountModel
   */
  AccountModel getAccountByAccountName(String accountName);
}