package io.vertx.ext.web.openapi.ui.types;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.openapi.impl.OpenAPIHolderImpl;
import io.vertx.ext.web.openapi.ui.OpenApiUiContext;
import io.vertx.ext.web.openapi.ui.listeners.OpenApiLoadedListener;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;

public class OpenAPIHolderImplRedefinition implements TypeRedefinition<OpenAPIHolderImpl> {

  @Override
  public DynamicType.Unloaded<OpenAPIHolderImpl> redefine(ByteBuddy byteBuddy) throws ClassNotFoundException {
    return byteBuddy.redefine(OpenAPIHolderImpl.class)
        .visit(Advice.to(OpenAPIHolderImplLoadOpenApiAdvice.class)
            .on(named("loadOpenAPI").and(takesArguments(String.class))))
        .make();
  }

  public static class OpenAPIHolderImplLoadOpenApiAdvice {

    @Advice.OnMethodExit
    public static void after(@Advice.Return Future<JsonObject> ret) {
      ret.onSuccess(OpenAPIHolderImplLoadOpenApiAdvice::hold);
    }

    public static void hold(JsonObject swaggerUi) {
      OpenApiLoadedListener.OpenApiLoadedEvent event = OpenApiLoadedListener.OpenApiLoadedEvent.builder()
          .swaggerUi(swaggerUi).build();
      OpenApiUiContext.fireEvent(event);
    }

  }

}
