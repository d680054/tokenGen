package com.pactera.adm.aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pactera.adm.TokenGen;
import com.pactera.adm.annotation.Header;
import com.pactera.adm.annotation.Param;
import com.pactera.adm.annotation.Token;
import com.pactera.adm.annotation.TokenRef;
import com.pactera.adm.timer.TokenDelay;
import com.pactera.adm.timer.TokenExpireTimer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David.Zheng on 23/11/2016.
 */
@Aspect
@Component
public class TokenGenAspect implements ApplicationContextAware
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenGenAspect.class);

	private ApplicationContext ctx;

	@Autowired
	private TokenExpireTimer tokenExpireTimer;

	@Pointcut("@annotation(com.pactera.adm.annotation.Token)")
	public void annotationPointCut1()
	{
	}

	@Pointcut("@annotation(com.pactera.adm.annotation.TokenRef)")
	public void annotationPointCut2()
	{

	}

	@Before("annotationPointCut1() || annotationPointCut2()")
	public void before(JoinPoint joinPoint) throws Exception
	{
		Token token = joinPoint.getTarget().getClass().getAnnotation(Token.class);

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		if (method.getAnnotation(TokenRef.class) == null)
		{
			token = method.getAnnotation(Token.class);
		}

		String key = DigestUtils.md5DigestAsHex(token.toString().getBytes());
		TokenDelay tokenDelay = new TokenDelay(key);
		if (!tokenExpireTimer.getDelayQueue().contains(tokenDelay))
		{
			tokenDelay =
					generateAccessToken(key, token.endPoint(), token.headers(), token.params(),
							token.respKeys());
			tokenExpireTimer.schedule(tokenDelay);
		}

		tokenExpireTimer.addIdentity(key);
	}

	private TokenDelay generateAccessToken(String key, String endPoint, Header[] headers, Param[] params,
			String[] respKeys)
			throws Exception
	{
		URI baseUri = new URI(findProp(endPoint));
		URIBuilder uriBuilder = new URIBuilder()
				.setHost(baseUri.getHost())
				.setScheme(baseUri.getScheme())
				.setPath(baseUri.getPath());

		Request request = Request.Post(uriBuilder.build());

		if (headers != null)
		{
			for (Header header : headers)
			{
				String name = findProp(header.name());
				String value = findProp(header.value());
				request.addHeader(name, value);
			}
		}

		if (params != null)
		{
			List postParameters = new ArrayList<NameValuePair>();
			for (Param param : params)
			{
				String name = findProp(param.name());
				String value = findProp(param.value());
				postParameters.add(new BasicNameValuePair(name, value));
			}
			request.bodyForm(postParameters);
		}

		Response response = request.execute();

		HttpResponse httpResponse = response.returnResponse();
		int statusCode = httpResponse.getStatusLine().getStatusCode();

		if (statusCode == HttpStatus.SC_OK)
		{
			String content = EntityUtils.toString(httpResponse.getEntity());
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode root = objectMapper.readTree(content);

			TokenDelay tokenDelay = new TokenDelay(key, Long.valueOf(root.get(TokenGen.EXPIRES_IN).asText("300")));
			tokenDelay.preDefRespKeys().addAll(Arrays.asList(respKeys));

			Map map = new HashMap();
			for (String respKey : tokenDelay.preDefRespKeys())
			{
				map.put(respKey, root.get(respKey) == null ? "" : root.get(respKey).textValue());
			}
			tokenDelay.setHashMap(map);

			return tokenDelay;
		}
		else
		{
			throw new RuntimeException("Not able to gain the access token.");
		}

	}

	private String findProp(String placeHolder)
	{
		String prop = placeHolder;
		if (prop.matches("^\\$\\{.+\\}$"))
		{
			Pattern pattern = Pattern.compile("[^${].+[^}]", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(placeHolder);
			while (matcher.find())
			{
				prop = matcher.group(0);
			}

			prop = ctx.getEnvironment().getProperty(prop);
		}

		return prop;
	}

	@Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.ctx = applicationContext;
	}
}
