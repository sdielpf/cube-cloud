package cn.philip.core.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * @description: app校验
 * @author: pfliu
 * @time: 2020/5/28
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD})
public @interface CheckApp {

}
