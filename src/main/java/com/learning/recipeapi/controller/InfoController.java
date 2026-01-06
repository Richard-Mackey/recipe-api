package com.learning.recipeapi.controller;

import com.learning.recipeapi.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class InfoController {

  private final AppConfig appConfig;
  private final Environment environment;

  @Autowired
  public InfoController(AppConfig appConfig, Environment environment) {
    this.appConfig = appConfig;
    this.environment = environment;
  }

  @GetMapping("/info")
  public Map<String, Object> getInfo() {
    Map<String, Object> info = new HashMap<>();
    info.put("name", appConfig.getAppName());
    info.put("version", appConfig.getAppVersion());
    info.put("port", appConfig.getServerPort());

    String[] profiles = environment.getActiveProfiles();
    info.put("profile", profiles.length > 0 ? profiles[0] : "default");

    return info;
  }
}
