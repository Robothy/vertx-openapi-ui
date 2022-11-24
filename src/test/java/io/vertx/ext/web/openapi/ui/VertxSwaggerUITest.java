package io.vertx.ext.web.openapi.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class VertxSwaggerUITest {

  @Test
  void test(VertxTestContext testContext) {
    Vertx vertx = Vertx.vertx();
    VertxSwaggerUI.install("/s");

    Checkpoint serverStarted = testContext.checkpoint();
    Checkpoint openApiCollected = testContext.checkpoint();
    Checkpoint openApiIndexOK = testContext.checkpoint();
    Checkpoint openApiBundleOK = testContext.checkpoint();

    RouterBuilder.create(vertx, "src/test/resources/openapi2.yaml");
    RouterBuilder.create(vertx, "src/test/resources/openapi.yaml")
        .compose(builder -> {
          builder.operations().forEach(it -> it.handler(routingContext ->
              routingContext.response().send(routingContext.request().method() + " " + routingContext.request().path())));
          Router router = builder.createRouter();

          int port = 19090;
          return vertx.createHttpServer().requestHandler(router)
              .listen(port, "localhost");
        })
        .onComplete(testContext.succeeding(v -> serverStarted.flag()))
        .onSuccess(v -> {
          testContext.verify(() -> assertEquals(2, OpenApiUiContext.getSwaggerUiBundle().size()));
          openApiCollected.flag();

          new Thread(() -> {
            Response response1 = RestAssured.given()
                .port(19090)
                .get("/pet/1");
            testContext.verify(() -> assertEquals(200, response1.statusCode()));
            openApiIndexOK.flag();

            Response response2 = RestAssured.given()
                .port(19090)
                .get("/s/" + OpenApiUiContext.getSwaggerUiBundle().keySet().iterator().next());
            testContext.verify(() -> assertEquals(200, response2.statusCode()));
            openApiBundleOK.flag();
          }).start();

        });
  }

}