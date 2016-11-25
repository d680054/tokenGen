package com.pactera.adm.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by David.Zheng on 25/11/2016.
 */
@Service
public class TokenExpireTimerImpl implements TokenExpireTimer
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenExpireTimerImpl.class);

	private DelayQueue<TokenDelay> delayQueue = new DelayQueue();

	private ThreadLocal threadLocal = new ThreadLocal();

	private ExecutorService delayPool = null;

	@PostConstruct void initMessageSender()
	{
		this.delayPool = Executors.newCachedThreadPool();
		this.delayPool.submit(this);
	}

	@PreDestroy
	public void cleanUp()
	{
		LOGGER.debug("clean up the delay pool.");
		if (null != this.delayPool)
		{
			this.delayPool.shutdownNow();
			this.delayPool = null;
		}
	}

	@Override public void run()
	{
		while (true)
		{
			try
			{
				delayQueue.take();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void schedule(TokenDelay tokenDelay)
	{
		if (!delayQueue.contains(tokenDelay))
		{
			delayQueue.add(tokenDelay);
		}
	}

	public DelayQueue getDelayQueue()
	{
		return this.delayQueue;
	}

	public void addIdentity(String key)
	{
		threadLocal.set(key);
	}

	public String getIdentity()
	{
		return (String)threadLocal.get();
	}

}
