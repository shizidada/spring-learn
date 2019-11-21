package org.excel.operator.security;

import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * <p>
 * Description
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2019 2019/11/20 21:20
 * @see org.excel.operator.config
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Resource
  private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

  @Resource
  private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

  @Resource
  private CustomLogoutSuccessHandler customLogoutSuccessHandler;

  @Resource
  private CustomLogoutHandler customLogoutHandler;

  @Resource
  private CustomAccessDeniedHandler customAccessDeniedHandler;

  @Resource
  private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  @Override protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().authorizeRequests();

    http.authorizeRequests()
        .antMatchers(HttpMethod.GET,
            "/",
            "/*.html",
            "/favicon.ico",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js",
            "/swagger-resources/**",
            "/v2/api-docs/**"
        ).permitAll()
        // druid 数据库监控
        .antMatchers("/druid/**").permitAll()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll();

    // 对登录注册要允许匿名访问
    http.authorizeRequests().antMatchers(
        // 注册
        "/api/v1/account/register",

        // 登录
        "/api/v1/account/login",
        "/api/v1/account/signin",

        // 退出
        "/api/v1/account/logout",
        "/api/v1/account/signout").permitAll();

    http.httpBasic().authenticationEntryPoint(customAuthenticationEntryPoint);

    http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);

    // 自定义登录
    http
        .formLogin()
        .loginProcessingUrl("/api/v1/account/signin")
        // 登录成功
        .successHandler(customAuthenticationSuccessHandler)
        // 登录失败
        .failureHandler(customAuthenticationFailureHandler);

    // 自定义登出成功
    http.logout()
        // 自定义 url
        .logoutUrl("/api/v1/account/signout")
        // 自定义登出成功返回
        .logoutSuccessHandler(customLogoutSuccessHandler)
        // 自定义登出成功
        .addLogoutHandler(customLogoutHandler)
        // 清理 Session
        .invalidateHttpSession(true);

    http.authorizeRequests().anyRequest().authenticated();
  }

  @Override protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(customUserDetailsService())
        .passwordEncoder(passwordEncoder());
  }

  /**
   * 设置用户密码的加密方式
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 自定义UserDetailsService，授权
   */
  @Bean
  public UserDetailsService customUserDetailsService() {
    return new UserDetailsServiceImpl();
  }

  /**
   * AuthenticationManager
   *
   * @throws Exception
   */
  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}
