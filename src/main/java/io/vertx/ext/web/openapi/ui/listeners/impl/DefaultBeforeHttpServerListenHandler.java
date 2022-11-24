package io.vertx.ext.web.openapi.ui.listeners.impl;


import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.ui.OpenApiUiContext;
import io.vertx.ext.web.openapi.ui.VertxSwaggerUI;
import io.vertx.ext.web.openapi.ui.listeners.BeforeHttpServerListenHandler;
import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultBeforeHttpServerListenHandler implements BeforeHttpServerListenHandler {

  @Override
  public void beforeHttpServerListen(BeforeHttpServerListenHandler.BeforeHttpServerRequestEvent event) {
    Handler<HttpServerRequest> requestHandler = event.getRouter();
    if (!(requestHandler instanceof Router)) {
      return;
    }

    Router router = (Router) requestHandler;

    router.route(HttpMethod.GET, VertxSwaggerUI.getOpenApiUiPath())
        .handler(new SwaggerIndexHandler());
    router.route(HttpMethod.GET, VertxSwaggerUI.getOpenApiUiPath() + "/:file")
        .handler(new SwaggerResourceHandler());
  }

  @Slf4j
  static class SwaggerIndexHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext context) {

      StringBuilder sb = new StringBuilder("[");
      String url = "{url: '%s', name: '%s'}";
      String urls = OpenApiUiContext.getSwaggerUiBundle().keySet()
          .stream().map(file -> url.formatted(VertxSwaggerUI.getOpenApiUiPath() + "/" + file, file))
          .collect(Collectors.joining(", \n"));
      sb.append(urls);
      sb.append("]");

      try (InputStream in = SwaggerIndexHandler.class.getClassLoader().getResourceAsStream("swagger/index.html")) {
        Objects.requireNonNull(in, "Swagger-UI not found.");

        if (OpenApiUiContext.getSwaggerUiBundle().isEmpty()) {
          throw new IllegalStateException("OpenAPI document not found.");
        }

        String template = new String(in.readAllBytes());
        String content = template.replace("{{swaggerUIBundle}}", sb.toString())
            .replace("{{openApiUiPath}}", VertxSwaggerUI.getOpenApiUiPath());
        context.response().send(content);
      } catch (Throwable e) {
        context.response().setStatusCode(500).send(e.getMessage());
        log.error("Failed to load swagger-ui", e);
      }

    }

  }


  static class SwaggerResourceHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext routingContext) {
      String file = routingContext.pathParam("file");
      if (OpenApiUiContext.getSwaggerUiBundle().containsKey(file)) {
        routingContext.response().send(OpenApiUiContext.getSwaggerUiBundle().get(file));
        return;
      }

      try (InputStream in = SwaggerResourceHandler.class.getClassLoader().getResourceAsStream("swagger/swaggerui/" + file)) {
        if (in == null) {
          throw new IllegalArgumentException(file + " not found.");
        }
        routingContext.response().send(Buffer.buffer(in.readAllBytes()));
      } catch (Throwable e) {

        if (e instanceof IllegalArgumentException) {
          routingContext.response().setStatusCode(400).send(e.getMessage());
        } else {
          routingContext.response().setStatusCode(500).send(e.getMessage());
        }
        log.error("Failed to get resource " + file, e);
      }
    }
  }

}
