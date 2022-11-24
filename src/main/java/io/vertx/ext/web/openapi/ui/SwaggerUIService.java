package io.vertx.ext.web.openapi.ui;

import io.vertx.core.impl.VertxBuilder;
import io.vertx.core.spi.VertxServiceProvider;

public class SwaggerUIService implements VertxServiceProvider {
  @Override
  public void init(VertxBuilder builder) {
    try {
      Class<?> clazz = Class.forName("io.vertx.ext.web.openapi.OpenAPIHolder");
      VertxSwaggerUI.install();
    } catch (ClassNotFoundException e) {
      // vertx-openapi module not exist.
    }

  }
}
