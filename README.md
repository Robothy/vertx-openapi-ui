# vertx-openapi-ui

vertx-openapi-ui is a Java component for Vert.X openapi projects. It exposes an HTTP endpoint `/swagger` that renders the
openapi documents with Swagger-UI.

## Usage

vertx-openapi-ui is a pluggable Java component. Just adding the below dependency to your Vert.X web project could
activate the functionality. You don't need to add any Java code to your sources; unless you want to do some customization.

+ **Step 1** Add the dependency to your `pom.xml` or `build.gradle`.

```xml
<dependency>
    <groupId>io.github.robothy</groupId>
    <artifactId>vertx-openapi-ui</artifactId>
</dependency>
```

+ **Step 2** Restart your Vert.X web app and visit endpoint `http://{host}/swagger`.

You can also customize the endpoint via `VertxSwaggerUI.install("/customized_endpoint")` before deploying the server.