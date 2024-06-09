package com.ramlearning.vertx.web.broker.assets;

import com.ramlearning.vertx.web.broker.AbstractRestApiTest;
import com.ramlearning.vertx.web.broker.MainVerticle;
import com.ramlearning.vertx.web.broker.config.ConfigLoader;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpResponseStatus.ACCEPTED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(VertxExtension.class)
public class TestAssetsRestApi extends AbstractRestApiTest {

  private static final Logger LOG = LoggerFactory.getLogger(TestAssetsRestApi.class);

  /*
   * Vertx Webclient is an asynchronous http client which is quite powerful. It can be used to call other rest apis
   * and for testing purposes. It can be used as a client for other rest APIs and also as a proxy.
   */
  @Test
  void return_all_assets(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(TEST_SERVER_PORT));

    /*
     * If you expect the test to be a success, use testContext.succeeding in onComplete. Else use testContext.failing.
     */

    client.get(AssetsRestApi.URL)
      .send()
      .onComplete(testContext.succeeding(response -> {
        var responseAsJson = response.bodyAsJsonArray();
        LOG.info("Response: {}", responseAsJson);
        assertEquals("[{\"name\":\"AAPL\"},{\"name\":\"AMZN\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"}," +
          "{\"name\":\"FCBK\"},{\"name\":\"GOOG\"},{\"name\":\"MSFT\"}]", responseAsJson.encode());
        assertEquals(200, response.statusCode());
        assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), response.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
        assertNotNull(response.getHeader("correlation_id"));
        testContext.completeNow();
      }));
  }
}
