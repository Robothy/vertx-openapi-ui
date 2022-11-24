package io.vertx.ext.web.openapi.ui.types;


import io.vertx.core.http.impl.HttpServerImpl;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.openapi.ui.OpenApiUiContext;
import io.vertx.ext.web.openapi.ui.listeners.BeforeHttpServerListenHandler;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerImplRedefinition implements TypeRedefinition<HttpServerImpl> {

  public static Logger log = LoggerFactory.getLogger(HttpServerImplRedefinition.class);

  @Override
  public DynamicType.Unloaded<HttpServerImpl> redefine(ByteBuddy byteBuddy) {
    return byteBuddy.redefine(HttpServerImpl.class)
        .visit(Advice.to(HttpServerImplListenAdvice.class).on(ElementMatchers.named("listen")
            .and(ElementMatchers.takesArguments(SocketAddress.class))))
        .make();
  }

  public static class HttpServerImplListenAdvice {

    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) SocketAddress address,
                             @Advice.This HttpServerImpl origin,
                             @Advice.FieldValue("vertx") VertxInternal vertxInternal) {
      try {
        BeforeHttpServerListenHandler.BeforeHttpServerRequestEvent event =
            BeforeHttpServerListenHandler.BeforeHttpServerRequestEvent.builder()
                .socketAddress(address)
                .router(origin.requestHandler())
                .vertx(vertxInternal)
                .build();
        OpenApiUiContext.fireEvent(event);
      } catch (Throwable e) {
        log.warn("Failed to emit BeforeHttpServerRequestEvent.", e);
      }
    }

  }

}
