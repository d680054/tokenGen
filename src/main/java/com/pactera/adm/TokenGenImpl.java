package com.pactera.adm;

import com.pactera.adm.timer.TokenDelay;
import com.pactera.adm.timer.TokenExpireTimer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

/**
 * Created by David.Zheng on 25/11/2016.
 */
@Service
public class TokenGenImpl implements TokenGen
{
	@Autowired
	private TokenExpireTimer tokenExpireTimer;

	public String getRespValue(String key)
	{
		String identity = tokenExpireTimer.getIdentity();
		Iterator<TokenDelay> it = tokenExpireTimer.getDelayQueue().iterator();
		while (it.hasNext())
		{
			TokenDelay tokenDelay = it.next();
			if (tokenDelay.getKey().equals(identity))
			{
				return tokenDelay.getValue(key);
			}
		}

		return null;
	}

}
