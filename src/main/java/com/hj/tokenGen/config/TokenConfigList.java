package com.hj.tokenGen.config;

import lombok.Data;

import java.util.List;

/**
 * @author David.Zheng
 * @date 2019-01-01
 */
@Data
//@ConfigurationProperties(prefix = "token-gen")
public class TokenConfigList {

    List<TokenConfig> configs;
}
