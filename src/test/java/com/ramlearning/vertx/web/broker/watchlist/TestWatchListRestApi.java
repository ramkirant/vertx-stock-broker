package com.ramlearning.vertx.web.broker.watchlist;

import com.ramlearning.vertx.web.broker.MainVerticle;
import com.ramlearning.vertx.web.broker.assets.Asset;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(TestWatchListRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  /*
   * Vertx Webclient is an asynchronous http client which is quite powerful. It can be used to call other rest apis
   * and for testing purposes. It can be used as a client for other rest APIs and also as a proxy.
   */
  @Test
  void adds_and_returns_watchlist_for_account(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));

    var accountId = UUID.randomUUID();
    /*
     * If you expect the test to be a success, use testContext.succeeding in onComplete. Else use testContext.failing.
     * For put request, use sendJsonObject() instead of send()
     */

    client.put("/account/watchlist/" + accountId.toString())
      .sendJsonObject(body())
      .onComplete(testContext.succeeding(response -> {
        var responseAsJson = response.bodyAsJsonObject();
        LOG.info("Response: {}", responseAsJson);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", responseAsJson.encode());
        assertEquals(200, response.statusCode());
        testContext.completeNow();
      }));
  }

  @Test
  void adds_and_deletes_watchlist_for_account(Vertx vertx, VertxTestContext testContext) {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));

    var accountId = UUID.randomUUID();
    /*
     * If you expect the test to be a success, use testContext.succeeding in onComplete. Else use testContext.failing.
     * For put request, use sendJsonObject() instead of send()
     */

    client.put("/account/watchlist/" + accountId.toString())
      .sendJsonObject(body())
      .onComplete(testContext.succeeding(response -> {
        var responseAsJson = response.bodyAsJsonObject();
        LOG.info("Response: {}", responseAsJson);
        assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", responseAsJson.encode());
        assertEquals(200, response.statusCode());
      }))
      .compose(next -> {
        client.delete("/account/watchlist/" + accountId.toString())
          .send()
          .onComplete(testContext.succeeding(response -> {
            var responseAsJson = response.bodyAsJsonObject();
            LOG.info("DELETE Response {}", responseAsJson);
            assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", responseAsJson.encode());
            assertEquals(200, response.statusCode());
            testContext.completeNow();
          }));
        return Future.succeededFuture();
      });
  }

  private static JsonObject body() {
    return new WatchList(Arrays.asList(
      new Asset("AMZN"),
      new Asset("TSLA"))
    ).toJsonObject();
  }
}
