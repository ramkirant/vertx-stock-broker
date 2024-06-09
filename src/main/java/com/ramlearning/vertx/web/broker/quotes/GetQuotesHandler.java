package com.ramlearning.vertx.web.broker.quotes;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetQuotesHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetQuotesHandler.class);

  private final Map<String, Quote> cachedQuotes;

  public GetQuotesHandler(final Map<String, Quote> cachedQuotes) {
    this.cachedQuotes = cachedQuotes;
  }

  @Override
  public void handle(RoutingContext context) {
    /* Access the path variable as below */
    final String assetParam = context.pathParam("asset");
    LOG.debug("Asset parameter: {}", assetParam);

    /* Send the response back */
    var maybeQuote = Optional.ofNullable(cachedQuotes.get(assetParam));
    if(maybeQuote.isEmpty()) {
      context.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(new JsonObject()
          .put("message","quote for asset " + assetParam + " not available")
          .put("path", context.normalizedPath())
          .toBuffer()
        );
      return;
    }
    final JsonObject response = maybeQuote.get().toJsonObject();
    LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(response.toBuffer());
  }
}
