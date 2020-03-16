package org.moose.account.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.moose.account.model.domain.PasswordDO;

/**
 * <p>
 * Description:
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2020-03-16 20:02:20:02
 * @see org.moose.account.mapper
 */
@Mapper
public interface PasswordMapper {

  /**
   * 添加密码
   *
   * @param passwordDO PasswordDO
   * @return 是否添加成功
   */
  int insert(PasswordDO passwordDO);
}
