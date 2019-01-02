package com.hj.tokenGen.service;

import com.hj.tokenGen.config.TokenConfig;
import com.hj.tokenGen.model.TokenResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * @author David.Zheng
 * @date 2019-01-01
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

    /**
     * @return
     */
    public TokenResp generateToken(TokenConfig tokenConfig) {
        log.debug("Generating access_code by client_id:" + tokenConfig.getClientId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap();
        multiValueMap.add(CLIENT_ID, tokenConfig.getClientId());
        multiValueMap.add(CLIENT_SECRET, tokenConfig.getClientSecret());
        multiValueMap.add(SCOPE, tokenConfig.getScope());
        multiValueMap.add(GRANT_TYPE, Optional.ofNullable(tokenConfig.getGrantType()).orElse(MODE_CLIENT_CREDENTIALS));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(multiValueMap, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<TokenResp> responseEntity = restTemplate.exchange(tokenConfig.getUrl(), HttpMethod.POST, request, TokenResp.class);
        TokenResp tokenResp = responseEntity.getBody();
        log.info("the token response: " + tokenResp);

        return tokenResp;
    }
}
