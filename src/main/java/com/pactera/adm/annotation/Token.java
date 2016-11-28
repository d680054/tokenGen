package com.pactera.adm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by David.Zheng on 23/11/2016.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
public @interface Token
{
	String endPoint();

	Header[] headers() default {};

	Param[] params() default {};

	String[] respKeys() default {"access_token", "expires_in", "scope", "token_type"};
}




