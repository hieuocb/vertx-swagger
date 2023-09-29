package com.nokanguyen.vertx.swagger.api;

import com.nokanguyen.vertx.swagger.model.Products;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class DemoApi {

  @Operation(summary = "Find all products", method = "GET", operationId = "products",
    tags = {
      "Product"
    },
    responses = {
      @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(
          mediaType = "application/json",
          encoding = @Encoding(contentType = "application/json"),
          schema = @Schema(name = "products", example = "{'products':[" +
            "{" +
            "'_id':'abc'," +
            "'title':'Red Truck'," +
            "'image_url':'https://images.pexels.com/photos/1112597/pexels-photo-1112597.jpeg'," +
            "'from_date':'2018-08-30'," +
            "'to_date':'2019-08-30'," +
            "'price':'125.00'," +
            "'enabled':true" +
            "}," +
            "{" +
            "'_id':'def'," +
            "'title':'Blue Truck'," +
            "'image_url':'https://images.pexels.com/photos/1117485/pexels-photo-1117485.jpeg'," +
            "'from_date':'2018-08-30'," +
            "'to_date':'2019-08-30'," +
            "'price':'250.00'," +
            "'enabled':true" +
            "}" +
            "]}",
            implementation = Products.class)
        )
      ),
      @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    }
  )
   public void demo(RoutingContext context) {
    JsonArray prods = new JsonArray();

    prods.add(productOne());
    prods.add(productTwo());

    context.response()
      .setStatusCode(200)
      .end(prods.encodePrettily());
   }

  private JsonObject productOne(){
    return new JsonObject()
      .put("_id", "abc")
      .put("title", "Red Truck")
      .put("image_url", "https://images.pexels.com/photos/1112597/pexels-photo-1112597.jpeg")
      .put("from_date", "2018-08-30")
      .put("to_date", "2019-08-30")
      .put("price", "125.00")
      .put("enabled", true);
  }

  private JsonObject productTwo() {
    return new JsonObject()
      .put("_id", "def")
      .put("title", "Blue Truck")
      .put("image_url", "https://images.pexels.com/photos/1117485/pexels-photo-1117485.jpeg")
      .put("from_date", "2018-08-30")
      .put("to_date", "2019-08-30")
      .put("price", "250.00")
      .put("enabled", false);
  }
}
