package org.excel.operator.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.excel.operator.entity.AccountDO;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/10/27 15:04
 * @see org.excel.operator.mapper
 */
@Mapper
public interface AccountMapper {

  /**
   * 根据用户名查找
   *
   * @param accountName
   *
   * @return
   */
  AccountDO findByAccountName(String accountName);

}