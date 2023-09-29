package com.nokanguyen.vertx.swagger.generator;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface VertxPath {
  String value() default "" ;
}
