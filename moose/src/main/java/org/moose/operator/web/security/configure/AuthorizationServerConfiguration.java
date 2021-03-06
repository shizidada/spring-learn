package org.moose.operator.web.security.configure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.moose.operator.web.security.granter.SmsCodeTokenGranter;
import org.moose.operator.web.service.AccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 资源认证配置
 * <p>
 * Description:
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @see org.moose.operator.web.security.configure
 * <p>
 * [/oauth/authorize] [/oauth/token] [/oauth/check_token] [/oauth/confirm_access] [/oauth/token_key]
 * [/oauth/error]
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

  /**
   * 注入用于支持 password 模式
   */
  @Resource
  private AuthenticationManager authenticationManager;

  @Resource
  private AccountService accountService;

  @Lazy
  @Resource(name = "userDetailsServiceBean")
  private UserDetailsService userDetailsService;

  @Resource
  private DataSource dataSource;

  @Resource
  private WebResponseExceptionTranslator customOAuth2ResponseExceptionTranslator;

  @Resource
  private RedisConnectionFactory redisConnectionFactory;

  @Resource
  private RedisTemplate<String, Object> redisTemplate;

  /**
   * 设置用户密码的加密方式
   */
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * 配置数据库数据源
   *
   * @return 数据库数据源
   */
  //@Bean
  //public DataSource dataSource() {
  //  // TODO:: DataSourceBuilder.create().build()
  //  return DruidDataSourceBuilder.create().build();
  //}

  /**
   * Token 持久化
   *
   * @return TokenStore
   */
  @Bean
  public TokenStore tokenStore() {
    // 保存在内存中
    // return new InMemoryTokenStore();
    // 基于 JDBC 实现，令牌保存到数据库
    //return new JdbcTokenStore(dataSource);
    // 基于 Redis 实现，令牌保存到 Redis
    return new RedisTokenStore(redisConnectionFactory);
    //return new JwtTokenStore(accessTokenConverter());
  }

  /**
   * A service that provides the details about an OAuth2 client.
   *
   * @return ClientDetailsService
   * <p>
   * 基于 JDBC 实现，需要事先在数据库配置客户端信息
   */
  @Bean
  public ClientDetailsService jdbcClientDetailsService() {
    return new JdbcClientDetailsService(dataSource);
  }

  /**
   * 添加自定义授权类型
   *
   * @return List<TokenGranter>
   */
  private TokenGranter tokenGranter(final AuthorizationServerEndpointsConfigurer endpoints) {
    List<TokenGranter> granters =
        new ArrayList<TokenGranter>(Collections.singletonList(endpoints.getTokenGranter()));

    SmsCodeTokenGranter smsCodeTokenGranter =
        new SmsCodeTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(),
            endpoints.getOAuth2RequestFactory());
    smsCodeTokenGranter.setRedisTemplate(redisTemplate);
    smsCodeTokenGranter.setAccountService(accountService);
    granters.add(smsCodeTokenGranter);
    return new CompositeTokenGranter(granters);
  }

  /**
   * Authorization Server endpoints.
   *
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints
        .tokenGranter(tokenGranter(endpoints))
        .tokenStore(tokenStore())
        .exceptionTranslator(customOAuth2ResponseExceptionTranslator);

    // 用于支持密码模式
    endpoints.authenticationManager(authenticationManager);

    // userDetailsService refresh_token
    endpoints.userDetailsService(userDetailsService);
  }

  /**
   * 授权服务安全配置
   *
   * @throws Exception
   */
  @Override
  public void configure(AuthorizationServerSecurityConfigurer oauthServer)
      throws Exception {
    oauthServer.passwordEncoder(passwordEncoder());

    oauthServer
        .tokenKeyAccess("permitAll()")
        .checkTokenAccess("permitAll()")
        .allowFormAuthenticationForClients();
  }

  /**
   * 授权客户端配置
   *
   * @throws Exception
   */
  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    // 客户端配置
    clients.withClientDetails(jdbcClientDetailsService());
  }
}
