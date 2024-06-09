package com.ramlearning.vertx.web.broker;

import com.ramlearning.vertx.web.broker.assets.AssetsRestApi;
import com.ramlearning.vertx.web.broker.config.BrokerConfig;
import com.ramlearning.vertx.web.broker.config.ConfigLoader;
import com.ramlearning.vertx.web.broker.quotes.QuotesRestApi;
import com.ramlearning.vertx.web.broker.watchlist.WatchListRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {

    System.setProperty(ConfigLoader.SERVER_PORT, "9000");

    var vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> {
      LOG.error("Unhandled: ", error);
    });
    vertx.deployVerticle(new MainVerticle(), ar -> {
      if(ar.failed()) {
        LOG.error("Failed to deploy: ", ar.cause());
        return;
      }
      LOG.info("Deployed {}!", MainVerticle.class.getName());
    });
  }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(configuration -> {
        LOG.info("Retrieved Configurations: {}", configuration);

        /*
         * Start the HTTP Server when the config load succeeds
         */

        startHttpServerAndAttachRoutes(startPromise, configuration);
      });


  }

  private void startHttpServerAndAttachRoutes(Promise<Void> startPromise, BrokerConfig configuration) {
    /*
     * Create a router if we want to make http calls.
     */
    final Router restApi = Router.router(vertx);
    restApi.route()
      .handler(BodyHandler.create()
        /*By default, there is no limit. If we want to set a limit, we can pass the number in bytes.*/
        //.setBodyLimit(1024)
        /*We can set the below property to true if we want to handle file uploads*/
        //.setHandleFileUploads(false)
      )
      .failureHandler(failureHandler());
    AssetsRestApi.attach(restApi);
    QuotesRestApi.attach(restApi);
    WatchListRestApi.attach(restApi);

    /*
     * Pass the router you have created to the http server in the request handler
     */
    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error -> LOG.error("HTTP Server error:", error))
      .listen(configuration.getServerPort(), http -> {
        if(http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port {}", configuration.getServerPort());
        } else {
          startPromise.fail(http.cause());
        }
      });

    /*vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        LOG.info("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });*/
  }

  private static Handler<RoutingContext> failureHandler() {
    return errorContext -> {
      if (errorContext.response().ended()) {
        /*
         * Ignore and do nothing. This would generally happen if the client stops the request
         */
        return;
      }
      LOG.error("Route Error: {}", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("messsage", "Something went wrong").toBuffer());
    };
  }
}
