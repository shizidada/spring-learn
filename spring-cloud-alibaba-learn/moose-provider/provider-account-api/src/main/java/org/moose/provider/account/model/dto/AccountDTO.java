package org.moose.provider.account.model.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.moose.commons.base.dto.BaseDTO;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2020 2020/3/16 23:19
 * @see org.moose.provider.account.model.dto
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountDTO extends BaseDTO implements Serializable {

  private static final long serialVersionUID = -5760620294163161236L;

  /**
   * 账号 ID
   */
  private Long accountId;

  /**
   * 账号名称
   */
  private String accountName;

  /**
   * 昵称
   */
  private String nickName;

  /**
   * 手机号码
   */
  private String phone;

  /**
   * 状态
   */
  private Integer status;

  /**
   * 账号角色
   */
  private String role;

  /**
   * 头像
   */
  private String icon;

  /**
   * 性别
   */
  private Integer gender;

  /**
   * 生日
   */
  private Date birthday;

  /**
   * 注册来源
   */
  private Integer sourceType;
}
