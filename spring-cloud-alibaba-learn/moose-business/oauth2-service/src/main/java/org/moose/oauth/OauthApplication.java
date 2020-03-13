package org.moose.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author taohua
 */
@SpringBootApplication
@EnableDiscoveryClient
public class OauthApplication {

  public static void main(String[] args) {
    SpringApplication.run(OauthApplication.class, args);
  }
}
