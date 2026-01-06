package com.learning.recipeapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Value("${app.name}")
  private String appName;

  @Value("${app.version}")
  private String appVersion;

  @Value("${server.port}")
  private int serverPort;

  public String getAppName() {
    return appName;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public int getServerPort() {
    return serverPort;
  }
}
