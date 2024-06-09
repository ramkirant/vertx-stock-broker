package com.ramlearning.vertx.web.broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class GetAssetsHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsHandler.class);

  @Override
  public void handle(RoutingContext context) {
    final JsonArray response = new JsonArray();
    /*
    response
      .add(new JsonObject().put("symbol", "AAPL"))
      .add(new JsonObject().put("symbol", "AMZN"))
      .add(new JsonObject().put("symbol", "NFLX"))
      .add(new JsonObject().put("symbol", "TSLA"));
    */

    /*
     * Custom Java Objects (Other than JsonObject or JsonArray) will only work if we add jackson-bind dependency
     */

    AssetsRestApi.ASSETS.stream().map(Asset::new).forEach((response::add));
    /*
    response
      .add(new Asset("AAPL"))
      .add(new Asset("AMZN"))
      .add(new Asset("NFLX"))
      .add(new Asset("TSLA"));
     */
    LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());

    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .putHeader("correlation_id", UUID.randomUUID().toString())
      .end(response.toBuffer());
  }
}
