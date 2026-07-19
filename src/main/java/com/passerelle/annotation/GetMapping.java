package com.passerelle.annotation;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMapping { 
    String value(); 
    String view() default ""; 
}