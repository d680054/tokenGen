package com.pactera.adm.aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pactera.adm.annotation.Header;
import com.pactera.adm.annotation.Param;
import com.pactera.adm.annotation.TokenGen;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David.Zheng on 23/11/2016.
 */
@Aspect
@Component
public class TokenGenAspect
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TokenGenAspect.class);

	@Autowired
	private TokenExpireTimer tokenExpireTimer;

	@Pointcut("@annotation(com.pactera.adm.annotation.TokenGen)")
	public void annotationPointCut()
	{
	}

	@Before("annotationPointCut()")
	public void before(JoinPoint joinPoint) throws Exception
	{
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		TokenGen tokenGen = method.getAnnotation(TokenGen.class);
		String key = DigestUtils.md5DigestAsHex(tokenGen.toString().getBytes());
		TokenDelay tokenDelay = new TokenDelay(key);
		if (!tokenExpireTimer.getDelayQueue().contains(tokenDelay))
		{
			tokenDelay =
					generateAccessToken(key, tokenGen.endPoint(), tokenGen.headers(), tokenGen.params());
			tokenExpireTimer.schedule(tokenDelay);
		}

		tokenExpireTimer.addIdentity(key);
	}

	private TokenDelay generateAccessToken(String key, String endPoint, Header[] headers, Param[] params)
			throws Exception
	{
		URI baseUri = new URI(endPoint);
		URIBuilder uriBuilder = new URIBuilder()
				.setHost(baseUri.getHost())
				.setScheme(baseUri.getScheme())
				.setPath(baseUri.getPath());

		Request request = Request.Post(uriBuilder.build());

		if (headers != null)
		{
			for (Header header : headers)
				request.addHeader(header.name(), header.value());
		}

		if (params != null)
		{
			List postParameters = new ArrayList<NameValuePair>();
			for (Param param : params)
			{
				postParameters.add(new BasicNameValuePair(param.name(), param.value()));
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
			String accessToken = root.get("access_token").asText();
			int expires = root.get("expires_in").asInt();

			return new TokenDelay(key, accessToken, expires);
		}
		else
		{
			throw new RuntimeException("Not able to gain the access token.");
		}

	}

}
