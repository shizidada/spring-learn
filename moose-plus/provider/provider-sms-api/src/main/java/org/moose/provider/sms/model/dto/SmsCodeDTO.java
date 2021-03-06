package org.moose.provider.sms.model.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.moose.commons.base.dto.BaseDTO;

/**
 *
 * <p>
 * Description:
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2020-04-01 14:09:14:09
 * @see org.moose.provider.sms.model.domain
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SmsCodeDTO extends BaseDTO implements Serializable {

  /**
   * 手机号码
   */
  private String phone;

  /**
   * 验证码
   */
  private String verifyCode;

  /**
   * 短信令牌
   */
  private String smsToken;

  /**
   * 发送短信类型
   */
  private String type;
}
