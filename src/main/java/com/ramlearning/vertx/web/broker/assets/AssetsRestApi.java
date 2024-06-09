package com.ramlearning.vertx.web.broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AssetsRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);
  public static final String URL = "/assets";

  public static final List<String> ASSETS = Arrays.asList("AAPL", "AMZN", "NFLX", "TSLA", "FCBK", "GOOG", "MSFT");
  public static void attach(Router parent) {
    parent.get(URL).handler(new GetAssetsHandler());
  }
}
