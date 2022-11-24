package io.vertx.ext.web.openapi.ui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.pool.TypePool;

public class VertxSwaggerUI {

  private static final String REDEFINITIONS_PATH = "/redefinitions";

  private static String SWAGGER_PATH = "/swagger";

  private static final AtomicBoolean installed = new AtomicBoolean();

  public static void install(String openApiUiPath) {
    SWAGGER_PATH = openApiUiPath;
    install();
  }

  public static String getOpenApiUiPath() {
    return SWAGGER_PATH;
  }

  public static void install() {

    if (installed.get()) {
      return;
    }

    synchronized (installed) {
      if (installed.get()) {
        return;
      }

      FileSystem fileSystem = null;
      try {
        URI uri = VertxSwaggerUI.class.getResource(REDEFINITIONS_PATH).toURI();
        TypePool typePool = TypePool.Default.ofSystemLoader();
        ByteBuddy byteBuddy = new ByteBuddy();

        Path myPath;
        if (uri.getScheme().equals("jar")) {
          fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
          myPath = fileSystem.getPath(REDEFINITIONS_PATH);
        } else {
          myPath = Paths.get(uri);
        }

        try (Stream<Path> walk = Files.walk(myPath, 1)) {
          for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
            Path path = it.next();
            if (Files.isDirectory(path)) {
              continue;
            }
            String redefinitionName = path.getFileName().toString();
            redefinitionName = redefinitionName.substring(0, redefinitionName.lastIndexOf('.')); // remove suffix
            byte[] redefinitionBytecode = Files.readAllBytes(path);

            TypeDescription typeDescription = typePool.describe(redefinitionName).resolve();
            ClassFileLocator classFileLocator = ClassFileLocator.Simple.of(redefinitionName, redefinitionBytecode);

            try (DynamicType.Unloaded<Object> unloaded = byteBuddy.redefine(typeDescription, classFileLocator).make()) {
              unloaded.load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION);
            }
          }
        }

        OpenApiUiContext.go();
      } catch (IOException | URISyntaxException e) {
        throw new IllegalStateException("Install vertx-web-autodoc failed.", e);
      } finally {
        if (null != fileSystem) {
          try {
            fileSystem.close();
          } catch (IOException e) {
            // Ignore
          }
        }
      }

      installed.set(true);
    }

  }

}
