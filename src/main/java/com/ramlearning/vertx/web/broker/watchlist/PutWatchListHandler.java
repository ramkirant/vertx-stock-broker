package com.ramlearning.vertx.web.broker.watchlist;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.UUID;

public class PutWatchListHandler implements Handler<RoutingContext> {

  final Map<UUID, WatchList> watchListPerAccount;

  public PutWatchListHandler(final Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }
  /*
   * To be able to pass a request body on the server side, we need to register a body handler.
   * Body Handler can only be registered per route. We can register body handler only for the watchlist router or
   * we can add it on the parent router.
   *
   * Vertx Web BodyHandler can be used to automatically parse and handle the body of incoming HTTP requests. It makes
   * it easy to handle the body of HTTP requests in our Vertx web application.
   *
   * It supports various ContentTypes including:
   * application/json
   * application/x-www-form-urlencoded
   * multipart/form-data
   * Plain text
   *
   * Key Features
   * 1. Automatic Parsing: BodyHandler automatically parses the request body based on the content type and makes it
   *    available for further processing.
   * 2. File Uploading: It handles File uploads by using multipart / form-data content type and it stores the files
   *    temporarily
   * 3. Memory Management: It manages memory usage by setting limits on the maximum size of the request body it can
   *    handle, preventing excessive memory usage.
   *
   * Configuration Options:
   * 1. setBodyLimit(long bodyLimit): Sets the maximum body size in bytes.
   * 2. setUploadsDirectory(String uploadsDirectory): Sets the directory where uploaded files will be stored.
   * 3. setMergeFormAttributes(boolean mergeFormAttributes): If set to true, form attributes will be added to the
   *    request parameters
   *
   * Once we add a BodyHandler, when a HTTP request reaches the WebServer, the BodyHandler will be called. if the
   * request contains a body, it will be added to the RequestContext. After that, the handler forwards it to the
   * next handler. If there is no next handler, the request ends. In our case, we have a custom handler that handles
   * the PUT request. So the body will be forwarded to our custom handler (attach method) and we can access the
   * body using context.body().asJsonObject() method.
   */

  @Override
  public void handle(RoutingContext context) {
    var accountId = WatchListRestApi.getAccountId(context);
    var bodyAsJson = context.body().asJsonObject();
    WatchList watchList = bodyAsJson.mapTo(WatchList.class);
    watchListPerAccount.put(UUID.fromString(accountId), watchList);
    //context.response().setStatusCode(HttpResponseStatus.OK.code());
    context.response()
      .setStatusCode(HttpResponseStatus.OK.code())
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(bodyAsJson.toBuffer());
  }
}
