package com.ramlearning.vertx.web.broker.config;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;

@Builder
@Value
@ToString
public class BrokerConfig {

  int serverPort;
  String version;

  public static BrokerConfig from(final JsonObject config){
    final Integer locServerPort = config.getInteger(ConfigLoader.SERVER_PORT);

    if(Objects.isNull(locServerPort)) {
      throw new RuntimeException(ConfigLoader.SERVER_PORT + " not configured!");
    }

    final String version = config.getString("version");
    if(Objects.isNull(version)) {
      throw new RuntimeException("version is not configured in config file");
    }
    return BrokerConfig.builder()
      .serverPort(locServerPort)
      .build();
  }
}
