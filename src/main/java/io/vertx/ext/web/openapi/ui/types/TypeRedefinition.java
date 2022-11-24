package io.vertx.ext.web.openapi.ui.types;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;

public interface TypeRedefinition<T> {

  DynamicType.Unloaded<T> redefine(ByteBuddy byteBuddy) throws ClassNotFoundException;

}
