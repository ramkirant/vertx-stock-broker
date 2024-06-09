package com.ramlearning.vertx.web.broker.quotes;

import com.ramlearning.vertx.web.broker.MainVerticle;
import com.ramlearning.vertx.web.broker.assets.AssetsRestApi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(TestQuotesRestApi.class);
  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  /*
   * Vertx Webclient is an asynchronous http client which is quite powerful. It can be used to call other rest apis
   * and for testing purposes. It can be used as a client for other rest APIs and also as a proxy.
   */
  @Test
  void returns_quote_for_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));

    /*
     * If you expect the test to be a success, use testContext.succeeding in onComplete. Else use testContext.failing.
     */

    client.get("/quotes/AMZN")
      .send()
      .onComplete(testContext.succeeding(response -> {
        var responseAsJson = response.bodyAsJsonObject();
        LOG.info("Response: {}", responseAsJson);
        assertEquals("{\"name\":\"AMZN\"}", responseAsJson.getJsonObject("asset").encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }

  @Test
  void returns_error_for_na_quote(Vertx vertx, VertxTestContext testContext) {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client.get("/quotes/UNKNOWN")
      .send()
      .onComplete(testContext.succeeding (response -> {
        var json = response.bodyAsJsonObject();
        LOG.info("Response {}", json);
        assertEquals(HttpResponseStatus.NOT_FOUND.code(), response.statusCode());
        assertEquals("{\"message\":\"quote for asset UNKNOWN not available\",\"path\":\"/quotes/UNKNOWN\"}", json.encode());
        testContext.completeNow();
      }));
  }
}
