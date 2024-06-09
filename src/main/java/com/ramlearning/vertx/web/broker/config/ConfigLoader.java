package com.ramlearning.vertx.web.broker.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ConfigLoader {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);
  public static final String SERVER_PORT = "SERVER_PORT";
  static final List<String> EXPOSED_ENVIRONMENT_VARIABLES = Arrays.asList(SERVER_PORT);
  public static final String APPLICATION_YML = "application.yml";

  public static Future<BrokerConfig> load(Vertx vertx) {

    final var exposedKeys = new JsonArray();
    EXPOSED_ENVIRONMENT_VARIABLES.forEach(exposedKeys::add);
    LOG.debug("Fetch configuration for {}", exposedKeys.encode());
    /*
     * By default, all the environment variables will be exposed to our application.
     * Also, the environment variables from the system will be exposed.
     * If we want to expose only certain environment variables, we can do so by using the setConfig method.
     */
    var envStore = new ConfigStoreOptions()
    /* env type will load the config from environment variables */
    .setType("env")
    .setConfig(new JsonObject().put("keys", exposedKeys));

    var propertyStore = new ConfigStoreOptions()
      /* env type sys will load the config from system properties */
      .setType("sys")
      /* For system properties, we have a config to enable or disable caching */
      .setConfig(new JsonObject().put("cache", false));

    var yamlStore = new ConfigStoreOptions()
      .setType("file")
      .setFormat("yaml")
      /*For yaml type, we need to set file path in the config*/
      .setConfig(new JsonObject().put("path", APPLICATION_YML));

    /*
     * We can use a ConfigRetriever to load our ConfigStoreOptions.
     */
    var retriever = ConfigRetriever.create(vertx,
      new ConfigRetrieverOptions()
        .addStore(yamlStore)
        .addStore(propertyStore)
        .addStore(envStore)
    );

    /*
     * retriever.getConfig() will return a vertx future so the config loading will be asynchronous.
     */
    return retriever.getConfig().map(BrokerConfig::from);
  }
}
