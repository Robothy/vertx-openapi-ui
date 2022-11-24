package io.vertx.ext.web.openapi.ui.listeners;

public interface AutodocListener<T extends AutodocEvent> {

  Class<T> eventType();

  default void emit(AutodocEvent event) {
    handle(eventType().cast(event));
  }

  void handle(T event);

}
