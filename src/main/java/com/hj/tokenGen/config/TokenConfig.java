package com.hj.tokenGen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;


/**
 * @author David.Zheng
 * @date 2018-12-30
 */
@Data
@Validated
@ConfigurationProperties(prefix = "token-gen")
public class TokenConfig {

  @NotBlank
  private String url;

  @NotBlank
  private String clientId;

  @NotBlank
  private String clientSecret;

  private String scope;

  private String grantType;

}
