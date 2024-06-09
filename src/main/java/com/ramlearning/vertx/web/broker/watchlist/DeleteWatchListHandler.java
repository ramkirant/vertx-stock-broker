package com.ramlearning.vertx.web.broker.watchlist;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {

  final Map<UUID, WatchList> watchListPerAccount;

  public DeleteWatchListHandler(final Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListRestApi.getAccountId(context);
    final WatchList deleted = watchListPerAccount.remove(UUID.fromString(accountId));

    context.response()
      .setStatusCode(HttpResponseStatus.OK.code())
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(deleted.toJsonObject().toBuffer());
  }
}
