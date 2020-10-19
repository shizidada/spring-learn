package org.moose.operator.web.controller;

import javax.annotation.Resource;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.moose.operator.common.api.R;
import org.moose.operator.model.params.UserInfoParam;
import org.moose.operator.web.service.AccountService;
import org.moose.operator.web.service.UserInfoService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author taohua 用户信息
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/user")
public class UserInfoController {

  @Resource
  private AccountService accountService;

  @Resource
  private UserInfoService userInfoService;

  @PostMapping(value = "/info")
  public R<Object> info() {
    return R.ok(userInfoService.getUserInfo(), "获取用户信息");
  }

  @PostMapping(value = "/update")
  public R<Object> update(@Valid UserInfoParam userInfoParam, BindingResult result) {
    return R.ok(userInfoService.updateUserInfo(userInfoParam), "更新用户信息");
  }

  @PostMapping(value = "/resetPhone")
  public R<Object> resetPhone(String phone, String smsCode) {
    return R.ok(userInfoService.resetPhone(phone, smsCode), "变更手机号码");
  }
}
