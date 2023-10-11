package com.nokanguyen.vertx.swagger.verticle;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.nokanguyen.vertx.swagger.MainVerticle;
import com.nokanguyen.vertx.swagger.api.DemoApi;
import com.nokanguyen.vertx.swagger.generator.OpenApiRoutePublisher;
import com.nokanguyen.vertx.swagger.generator.Required;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nokanguyen.vertx.swagger.generator.AnnotationMappers.mapParameters;

public class WebAppVerticle extends AbstractVerticle {

  private static final String APPLICATION_JSON = "application/json";
  private static final int PORT = 8080;
  private static final String HOST = "localhost";
  private HttpServer server;
  private DemoApi demoApi;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    demoApi = new DemoApi();
    server = vertx.createHttpServer(createOption());
    server.requestHandler(configurationRouter());
    server.listen(result -> {
      if (result.succeeded()) {
        startPromise.complete();
      } else {
        startPromise.fail(result.cause());
      }
    });
  }

  private HttpServerOptions createOption() {
    var options = new HttpServerOptions();
    options.setHost(HOST);
    options.setPort(PORT);
    return options;
  }

  private Router configurationRouter() {
    var router = Router.router(vertx);
    router.get("/products").handler(demoApi::demo);

    OpenAPI openAPIDoc = OpenApiRoutePublisher.publishOpenApiSpec(
      router,
      "spec",
      "Vertx Swagger Auto Generation",
      "1.0.0",
      "http://" + HOST + ":" + PORT + "/"
    );

    openAPIDoc.addTagsItem( new io.swagger.v3.oas.models.tags.Tag().name("Product").description("Product operations"));


    // Generate the SCHEMA section of Swagger, using the definitions in the Model folder
    ImmutableSet<ClassPath.ClassInfo> modelClasses = getClassesInPackage("io.vertx.VertxAutoSwagger.Model");

    Map<String, Object> map = new HashMap<String, Object>();

    for(ClassPath.ClassInfo modelClass : modelClasses){

      Field[] fields = FieldUtils.getFieldsListWithAnnotation(modelClass.load(), Required.class).toArray(new
        Field[0]);
      List<String> requiredParameters = new ArrayList<String>();

      for(Field requiredField : fields){
        requiredParameters.add(requiredField.getName());
      }

      fields = modelClass.load().getDeclaredFields();

      for (Field field : fields) {
        mapParameters(field, map);
      }

      openAPIDoc.schema(modelClass.getSimpleName(),
        new Schema()
          .title(modelClass.getSimpleName())
          .type("object")
          .required(requiredParameters)
          .properties(map)
      );

      map = new HashMap<String, Object>();
    }
    //
    router.get("/swagger").handler(res -> {
      res.response()
        .setStatusCode(200)
        .end(Json.pretty(openAPIDoc));
    });

    // Serve the Swagger UI out on /doc/index.html
    router.route("/doc/*").handler(StaticHandler.create().setCachingEnabled(false).setWebRoot("webroot/swagger-ui"));


    return router;
  }

  private Boolean isPrimitiveOrWrapper(Type type){
    return type.equals(Double.class) ||
      type.equals(Float.class) ||
      type.equals(Long.class) ||
      type.equals(Integer.class) ||
      type.equals(Short.class) ||
      type.equals(Character.class) ||
      type.equals(Byte.class) ||
      type.equals(Boolean.class) ||
      type.equals(String.class);
  }

  public ImmutableSet<ClassPath.ClassInfo> getClassesInPackage(String pckgname) {
    try {
      ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
      ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClasses(pckgname);
      return classes;

    } catch (Exception e) {
      return null;
    }
  }
}
