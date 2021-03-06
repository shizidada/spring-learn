package org.moose.operator.configure;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author taohua
 */
@Configuration
public class RedisConfiguration {

  //@Bean
  //public RedisConnectionFactory redisConnectionFactory() {
  //  RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
  //  redisStandaloneConfiguration.setHostName("localhost");
  //  redisStandaloneConfiguration.setDatabase(0);
  //  redisStandaloneConfiguration.setPort(6379);
  //  JedisConnectionFactory factory = new JedisConnectionFactory(redisStandaloneConfiguration);
  //  return factory;
  //}

  //@Bean
  //public JedisPoolConfig jedisPoolConfig() {
  //  JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
  //  jedisPoolConfig.setMaxIdle(10);
  //  jedisPoolConfig.setMinIdle(2);
  //  return jedisPoolConfig;
  //}

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
    template.setConnectionFactory(factory);
    Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
        new Jackson2JsonRedisSerializer<Object>(Object.class);
    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    om.activateDefaultTyping(om.getPolymorphicTypeValidator(),
        ObjectMapper.DefaultTyping.NON_FINAL);

    // 解决jackson2无法反序列化LocalDateTime的问题
    om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    om.registerModule(new JavaTimeModule());
    jackson2JsonRedisSerializer.setObjectMapper(om);

    // value 序列化方式采用 jackson
    template.setValueSerializer(jackson2JsonRedisSerializer);
    // hash 的 value 序列化方式采用 jackson
    template.setHashValueSerializer(jackson2JsonRedisSerializer);

    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    // key 采用 String 的序列化方式
    template.setKeySerializer(stringRedisSerializer);
    // hash 的 key 也采用 String 的序列化方式
    template.setHashKeySerializer(stringRedisSerializer);
    template.afterPropertiesSet();
    return template;
  }
}
