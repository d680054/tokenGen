package com.hj.tokenGen.starter;

import com.hj.tokenGen.config.TokenConfig;
import com.hj.tokenGen.model.TokenResp;
import com.hj.tokenGen.service.TokenService;
import com.hj.tokenGen.service.TokenServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author David.Zheng
 * @date 2018-12-30
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(TokenConfig.class)
@Import({TokenServiceImpl.class, RestTemplateInitializer.class})
public class TokenGenAutoConfiguration {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenConfig tokenConfig;

    private Map<String, String> tokenMap = new ConcurrentHashMap<>();

    private ExpiringMap<String, TokenConfig> tokenCache = ExpiringMap.builder()
            .asyncExpirationListener((key, value) -> {
                log.info("expiring the key:" + key);
                this.storeNewAccessToken((String) key, (TokenConfig) value);
            }).variableExpiration().build();

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    /**
     * build the restTemplate interceptor,
     * generate the access token if empty,
     * add the access token into the request header if the request host equals to token authentication host
     *
     * @return
     */
    @Bean("oauthInterceptor")
    public ClientHttpRequestInterceptor buildInterceptor() {
        return (request, body, execution) -> {
            String authHost = new URL(tokenConfig.getUrl()).getHost();
            String requestHost = request.getURI().getHost();
            if (StringUtils.pathEquals(authHost, requestHost)) {
                if (!tokenMap.containsKey(authHost)) {
                    storeNewAccessToken(authHost, tokenConfig);
                }
                request.getHeaders().add("Authorization", "Bearer " + tokenMap.get(authHost));
            }
            ClientHttpResponse response = execution.execute(request, body);

            return response;
        };
    }

    /**
     * build the access token based on the token config property
     * cache the access token by token authentication host name.
     *
     * @param authHost
     * @param tokenConfig
     * @return
     */
    private String storeNewAccessToken(String authHost, TokenConfig tokenConfig) {
        TokenResp tokenResp = tokenService.generateToken(tokenConfig);
        String accessToken = tokenResp.getAccessToken();
        tokenMap.put(authHost, accessToken);
        tokenCache.put(authHost, tokenConfig, ExpirationPolicy.CREATED, Long.valueOf(tokenResp.getExpiresIn()) - 3539, TimeUnit.SECONDS);
        log.debug("Generated new token: " + accessToken);

        return accessToken;
    }

}
