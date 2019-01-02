package com.hj.tokenGen.starter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

/**
 * @author David.Zheng
 * @date 2019-01-02
 */
@Configuration
public class RestTemplateInitializer implements ApplicationContextAware {

    private ApplicationContext context;

    @Autowired
    @Qualifier("oauthInterceptor")
    private ClientHttpRequestInterceptor oauthInterceptor;


    @PostConstruct
    public void addOAuthInterceptor() {
        RestTemplate restTemplate = context.getBean(RestTemplate.class);
        restTemplate.getInterceptors().add(oauthInterceptor);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
