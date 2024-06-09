package com.ramlearning.vertx.web.broker.quotes;

import com.ramlearning.vertx.web.broker.assets.Asset;
import com.ramlearning.vertx.web.broker.assets.AssetsRestApi;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(QuotesRestApi.class);

  /*
   * Pass the path variable with :asset
   */
  public static void attach(Router parent) {
    final Map<String, Quote> cachedQuotes = new HashMap<String, Quote>();

    AssetsRestApi.ASSETS.forEach(symbol -> {
      cachedQuotes.put(symbol, initRandomQuote(symbol));
    });

    parent.get("/quotes/:asset").handler(new GetQuotesHandler(cachedQuotes));
  }

  private static Quote initRandomQuote(String assetParam) {
    /* We were able to build this because we have used lombok's builder annotation*/
    return Quote.builder()
      .asset(new Asset(assetParam))
      .ask(randomValue())
      .bid(randomValue())
      .lastPrice(randomValue())
      .volume(randomValue())
      .build();
  }

  private static Integer randomValue() {
    return ThreadLocalRandom.current().nextInt(1, 100);
  }
}
