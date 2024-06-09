package com.ramlearning.vertx.web.broker.watchlist;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WatchListRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);

  public static void attach(final Router parent) {
    final Map<UUID, WatchList> watchListPerAccount = new HashMap<UUID, WatchList>();

    final String PATH = "/account/watchlist/:accountId";

    parent.get(PATH).handler(new GetWatchListHandler(watchListPerAccount));

    parent.put(PATH).handler(new PutWatchListHandler(watchListPerAccount));

    parent.delete(PATH).handler(new DeleteWatchListHandler(watchListPerAccount));
  }

  static String getAccountId(RoutingContext context) {
    var accountId = context.pathParam("accountId");
    LOG.debug("{} for account {}", context.normalizedPath(), accountId);
    return accountId;
  }
}
