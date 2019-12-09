package org.excel.operator.entity;

import java.util.Date;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/11/17 20:43
 * @see org.excel.operator.entity
 */
@Data
public class BaseDO {

  /**
   * 创建时间
   */
  @Field(type = FieldType.Date)
  private Date createTime;

  /**
   * 修改时间
   */
  @Field(type = FieldType.Date)
  private Date updateTime;
}
