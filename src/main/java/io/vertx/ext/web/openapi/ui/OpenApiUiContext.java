package io.vertx.ext.web.openapi.ui;

import io.vertx.ext.web.openapi.ui.listeners.AutodocEvent;
import io.vertx.ext.web.openapi.ui.listeners.AutodocEventDispatcher;
import io.vertx.ext.web.openapi.ui.listeners.AutodocListener;
import io.vertx.ext.web.openapi.ui.listeners.impl.DefaultAutodocEventDispatcher;
import io.vertx.ext.web.openapi.ui.listeners.impl.DefaultBeforeHttpServerListenHandler;
import io.vertx.ext.web.openapi.ui.listeners.impl.DefaultOpenApiLoadedListener;
import java.util.HashMap;
import java.util.Map;

public class OpenApiUiContext {

  private static final AutodocEventDispatcher dispatcher = new DefaultAutodocEventDispatcher();

  private static final Map<String, String> swaggerUiBundle = new HashMap<>();

  public static Map<String, String> getSwaggerUiBundle() {
    return swaggerUiBundle;
  }

  public static <E extends AutodocEvent> void addListener(AutodocListener<E> listener) {
    dispatcher.addListener(listener);
  }

  public static void fireEvent(AutodocEvent event) {
    dispatcher.dispatch(event);
  }

  static void go() {
    addListener(new DefaultBeforeHttpServerListenHandler());
    addListener(new DefaultOpenApiLoadedListener());
  }

}
