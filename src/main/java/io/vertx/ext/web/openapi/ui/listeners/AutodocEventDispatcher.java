package io.vertx.ext.web.openapi.ui.listeners;

public interface AutodocEventDispatcher {

  void dispatch(AutodocEvent event);

  <E extends AutodocEvent> void addListener(AutodocListener<E> listener);

}
