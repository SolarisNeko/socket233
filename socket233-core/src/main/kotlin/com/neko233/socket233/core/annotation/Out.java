package com.neko233.socket233.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 输出对象, 该引用后续还在使用
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Out {

    String tips() default "";

}
