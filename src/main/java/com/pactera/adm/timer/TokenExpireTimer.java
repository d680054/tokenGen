package com.pactera.adm.timer;

import java.util.concurrent.DelayQueue;

/**
 * Created by David.Zheng on 25/11/2016.
 */
public interface TokenExpireTimer extends Runnable
{
	void schedule(TokenDelay tokenDelay);

	DelayQueue getDelayQueue();

	void addIdentity(String key);

	String getIdentity();
}
