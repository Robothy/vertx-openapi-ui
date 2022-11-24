package io.vertx.ext.web.openapi.ui;

import io.vertx.ext.web.openapi.ui.types.HttpServerImplRedefinition;
import io.vertx.ext.web.openapi.ui.types.OpenAPIHolderImplRedefinition;
import io.vertx.ext.web.openapi.ui.types.TypeRedefinition;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;

class GenerateTypes {

  private static final String REDEFINITIONS_DEST = "src/main/resources/redefinitions";

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    TypeRedefinition<?>[] typeDefinitions = new TypeRedefinition[] {
        new HttpServerImplRedefinition(),
        new OpenAPIHolderImplRedefinition()
    };

    String dest = args.length == 0 ? REDEFINITIONS_DEST : args[0];

    ByteBuddy byteBuddy = new ByteBuddy();
    new File(dest).mkdirs();
    for (TypeRedefinition<?> redefinition : typeDefinitions) {
      try (DynamicType.Unloaded<?> redefine = redefinition.redefine(byteBuddy)) {
        Files.write(Paths.get(dest, redefine.getTypeDescription().getName() + ".class"), redefine.getBytes());
      }
    }
  }

}
