package com.nokanguyen.vertx.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Products {
  @JsonProperty("products")
  public Product[] products;
}
