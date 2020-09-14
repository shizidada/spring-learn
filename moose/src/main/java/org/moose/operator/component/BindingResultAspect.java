package org.moose.operator.component;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.moose.operator.common.api.ResultCode;
import org.moose.operator.exception.BusinessException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * HibernateValidator 错误结果处理切面
 *
 * @author taohua
 */
@Aspect
@Component
@Order(2)
public class BindingResultAspect {
  @Pointcut("execution(public * org.excel.operator.web.controller.*.*(..))")
  public void validateAnnotation() {
  }

  @Around("validateAnnotation()")
  public Object doAround(ProceedingJoinPoint point) throws Throwable {
    Object[] args = point.getArgs();
    for (Object arg : args) {
      if (arg instanceof BindingResult) {
        BindingResult result = (BindingResult) arg;
        if (result.hasErrors()) {
          FieldError fieldError = result.getFieldError();
          String message = ResultCode.PARAMS_VALIDATE_FAIL.getMessage();
          if (fieldError != null) {
            message = fieldError.getDefaultMessage();
          }
          throw new BusinessException(ResultCode.PARAMS_VALIDATE_FAIL, message);
        }
      }
    }
    return point.proceed();
  }
}