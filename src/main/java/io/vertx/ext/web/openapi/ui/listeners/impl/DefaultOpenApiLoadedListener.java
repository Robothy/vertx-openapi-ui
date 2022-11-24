package io.vertx.ext.web.openapi.ui.listeners.impl;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.openapi.ui.OpenApiUiContext;
import io.vertx.ext.web.openapi.ui.listeners.OpenApiLoadedListener;

public class DefaultOpenApiLoadedListener implements OpenApiLoadedListener {

  @Override
  public void handle(OpenApiLoadedEvent event) {
    JsonObject swaggerUi = event.getSwaggerUi();
    String fileName = System.identityHashCode(swaggerUi) + ".json";
    OpenApiUiContext.getSwaggerUiBundle().put(fileName, swaggerUi.toString());
  }

}
