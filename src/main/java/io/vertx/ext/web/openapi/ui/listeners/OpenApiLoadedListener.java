package io.vertx.ext.web.openapi.ui.listeners;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public interface OpenApiLoadedListener extends AutodocListener<OpenApiLoadedListener.OpenApiLoadedEvent> {

  @Override
  default Class<OpenApiLoadedEvent> eventType() {
    return OpenApiLoadedEvent.class;
  }

  @Builder
  @Getter
  class OpenApiLoadedEvent implements AutodocEvent {

    private JsonObject swaggerUi;

  }

}
