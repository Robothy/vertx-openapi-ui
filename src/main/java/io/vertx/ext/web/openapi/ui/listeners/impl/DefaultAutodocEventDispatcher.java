package io.vertx.ext.web.openapi.ui.listeners.impl;


import io.vertx.ext.web.openapi.ui.listeners.AutodocEvent;
import io.vertx.ext.web.openapi.ui.listeners.AutodocEventDispatcher;
import io.vertx.ext.web.openapi.ui.listeners.AutodocListener;
import java.util.LinkedList;

public class DefaultAutodocEventDispatcher implements AutodocEventDispatcher {

  private final LinkedList<AutodocListener<? extends AutodocEvent>> listeners = new LinkedList<>();

  @Override
  public void dispatch(AutodocEvent event) {
    listeners.forEach(listener -> {
      if (listener.eventType() == event.getClass()) {
        AutodocEvent e = listener.eventType().cast(event);
        listener.emit(e);
      }
    });
  }

  @Override
  public <E extends AutodocEvent> void addListener(AutodocListener<E> listener) {
    listeners.addLast(listener);
  }
}
