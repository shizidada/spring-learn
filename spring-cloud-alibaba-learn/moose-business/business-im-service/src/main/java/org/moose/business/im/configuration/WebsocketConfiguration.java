package org.moose.business.im.configuration;

import javax.annotation.Resource;
import org.moose.business.im.websocket.handler.PushMessageHandler;
import org.moose.business.im.websocket.interceptor.MessageHandshakeInterceptor;
import org.moose.commons.base.snowflake.SnowflakeIdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 *
 * <p>
 * Description:
 * </p>
 *
 * @author taohua
 * @version v1.0.0
 * @date 2020-04-08 14:31:14:31
 * @see org.moose.business.im.configuration
 */
@Configuration
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketConfigurer {

  @Resource
  private PushMessageHandler pushMessageHandler;

  //@Resource
  //private NormalMessageHandler messageHandler;

  @Resource
  private MessageHandshakeInterceptor messageHandshakeInterceptor;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        //.addHandler(this.messageHandler, "/ws/{accountId}")
        .addHandler(this.pushMessageHandler, "/ws/{accountId}")
        .setAllowedOrigins("*")
        .addInterceptors(this.messageHandshakeInterceptor);
  }

  @Bean
  SnowflakeIdWorker snowflakeIdWorker() {
    return new SnowflakeIdWorker(7, 19);
  }
}
