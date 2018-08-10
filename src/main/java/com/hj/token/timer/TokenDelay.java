package com.hj.token.timer;

import com.hj.token.TokenGen;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by David.Zheng on 24/11/2016.
 */
public class TokenDelay implements Delayed
{
	private String key;

	private long endTime;

	private Map<String, String> hashMap;

	private List<String> preDefResplist =
			new ArrayList<String>(Arrays.asList(TokenGen.ACCESS_TOKEN, TokenGen.EXPIRES_IN, TokenGen.TOKEN_TYPE));

	public TokenDelay(String key)
	{
		this.key = key;
	}

	public TokenDelay(String key, long expiresIn)
	{
		this.key = key;
		this.endTime = System.currentTimeMillis() + expiresIn * 1000 - 60 * 1000;
	}

	public String getKey()
	{
		return key;
	}

	@Override public long getDelay(TimeUnit unit)
	{
		return endTime - System.currentTimeMillis();
	}

	@Override public int compareTo(Delayed o)
	{
		TokenDelay tokenDelay = (TokenDelay) o;
		return endTime - tokenDelay.endTime > 0 ? 1 : 0;
	}

	@Override public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		TokenDelay that = (TokenDelay) o;

		return new org.apache.commons.lang3.builder.EqualsBuilder()
				.append(key, that.key)
				.isEquals();
	}

	@Override public int hashCode()
	{
		return new HashCodeBuilder(17, 37)
				.append(key)
				.toHashCode();
	}

	public String getValue(String key)
	{
		return hashMap.get(key);
	}

	public void setHashMap(Map hashMap)
	{
		this.hashMap = hashMap;
	}

	public List<String> preDefRespKeys()
	{
		return preDefResplist;
	}
}
