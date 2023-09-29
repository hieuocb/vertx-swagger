package com.nokanguyen.vertx.swagger;

import com.nokanguyen.vertx.swagger.verticle.WebAppVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    //super.start(startPromise);
    var options = new DeploymentOptions().setWorker(true);
    vertx.deployVerticle(WebAppVerticle.class.getName(), options, result -> {
      if (result.succeeded()) {
        startPromise.complete();
      } else {
        startPromise.fail(result.cause());
      }
    });
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    //super.stop(stopPromise);
    vertx.undeploy(WebAppVerticle.class.getSimpleName(), handler -> {
      if (handler.succeeded()) {
        stopPromise.complete();
      } else {
        stopPromise.fail(handler.cause());
      }
    });
  }
}
