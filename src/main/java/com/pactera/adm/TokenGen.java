package com.pactera.adm;

/**
 * Created by David.Zheng on 25/11/2016.
 */
public interface TokenGen
{
	public static final String ACCESS_TOKEN = "access_token";

	public static final String EXPIRES_IN = "expires_in";

	public static final String TOKEN_TYPE = "token_type";

	String getRespValue(String key);
}
