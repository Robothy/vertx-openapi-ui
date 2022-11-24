package io.vertx.ext.web.openapi.ui.listeners;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import lombok.Builder;
import lombok.Getter;

/**
 * Before {@linkplain io.vertx.core.http.HttpServer#listen(SocketAddress)}.
 */
public interface BeforeHttpServerListenHandler extends AutodocListener<BeforeHttpServerListenHandler.BeforeHttpServerRequestEvent> {

  void beforeHttpServerListen(BeforeHttpServerRequestEvent event);

  @Override
  default void handle(BeforeHttpServerRequestEvent event) {
    beforeHttpServerListen(event);
  }

  @Override
  default Class<BeforeHttpServerRequestEvent> eventType() {
    return BeforeHttpServerRequestEvent.class;
  }

  @Getter
  @Builder
  class BeforeHttpServerRequestEvent implements AutodocEvent {

    private SocketAddress socketAddress;

    private Vertx vertx;

    private Handler<HttpServerRequest> router;

  }

}
