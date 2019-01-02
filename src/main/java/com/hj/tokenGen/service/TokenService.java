package com.hj.tokenGen.service;

import com.hj.tokenGen.config.TokenConfig;
import com.hj.tokenGen.model.TokenResp;

/**
 * @author David.Zheng
 * @date 2019-01-01
 */
public interface TokenService {

    String CLIENT_ID = "client_id";

    String CLIENT_SECRET = "client_secret";

    String SCOPE = "scope";

    String GRANT_TYPE = "grant_type";

    String MODE_CLIENT_CREDENTIALS = "client_credentials";

    TokenResp generateToken(TokenConfig tokenConfig);
}
