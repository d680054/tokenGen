# Introduction

This is a byproduct when I develop the log-watch project. This project helps you to reduce the duplicated work when you're facing the REST API Token-based Authentication. 

# Project Clone：
```
Source: git clone https://davidzheng1022@bitbucket.org/davidzheng1022/token-gen.git
Wiki: https://bitbucket.org/davidzheng1022/token-gen/wiki
```

# Features

* Annotation based, less code
* No more access token generated if the existing token has not been expired
* Multiple different token support, smart identification
* property placeholder support
* customised json response key


# Quick Start
* Download the code and run 'mvn install'

* In your project, add the dependency to your project pom file
```
    <dependency>
	<groupId>com.hj.token</groupId>
	<artifactId>token-gen</artifactId>
	<version>1.0</version>
    </dependency>
```
* Help Spring to find it:
   in spring-boot project, add @ComponentScan({"com.hj.token"})
   in other spring project, <context:component-scan base-package="com.hj.token" />

*  config the annotation before any API call, see examples:


#Example

*** example 1:**Put the annotation @Token above the API method. It supports the placeholder, you can put the value in the property file.
```
   @Autowired
   private TokenGen tokenGen;

   @Token(endPoint = "${telstra.sms.api.gateway}", params = {
			@Param(name = "client_id", value = "${spring.tokenGen.client}"),
			@Param(name = "client_secret", value = "fCkXFVEmEK2Qbexg"),
			@Param(name = "scope", value = "SMS"),
			@Param(name = "grant_type", value = "client_credentials") })
   public void getUserNameAPI(){
      ....
      String accessToken = tokenGen.getRespValue(TokenGen.ACCESS_TOKEN);
      ....
   }
```

*** example 2:** If you have more than one API method, put the annotation @Token above the class,
             put the indicator @TokenRef above the method
```
@Service
@Token(endPoint = "${telstra.sms.api.gateway}", params = {
			@Param(name = "client_id", value = "${spring.tokenGen.client}"),
			@Param(name = "client_secret", value = "fCkXFVEmEK2Qbexg"),
			@Param(name = "scope", value = "SMS"),
			@Param(name = "grant_type", value = "client_credentials") })
public class TestServiceImpl implements TestService {
   @Autowired
   private TokenGen tokenGen;

   @TokenRef
   public void getUserNameAPI() {
       ....
       String accessToken = tokenGen.getRespValue(TokenGen.ACCESS_TOKEN);
       ....
   }

   @TokenRef
   public void getUserUsageAPI() {
      ....
      String accessToken = tokenGen.getRespValue(TokenGen.ACCESS_TOKEN);
      ....
   }
}

```

*** example 3:** The @Token above the method will override the @Token above the class,
            the getUserNameAPI and getUserUsageAPI will use gateway1 to send the message,
            the getUserSubscriptionAPI will use different config
```
@Service
@Token(endPoint = "${telstra.sms.api.gateway1}", params = {
			@Param(name = "client_id", value = "${spring.tokenGen.client}"),
			@Param(name = "client_secret", value = "fCkXFVEmEK2Qbexg"),
			@Param(name = "scope", value = "SMS"),
			@Param(name = "grant_type", value = "client_credentials") })
public class TestServiceImpl implements TestService {
   @Autowired
   private TokenGen tokenGen;

   @TokenRef
   public void getUserNameAPI() {
       ....
       String accessToken = tokenGen.getRespValue(TokenGen.ACCESS_TOKEN);
       ....
   }

   @TokenRef
   public void getUserUsageAPI() {
      ....
      String accessToken = tokenGen.getRespValue(TokenGen.ACCESS_TOKEN);
      ....
   }


   @Token(endPoint = "${telstra.sms.api.gateway2}", params = {
      			@Param(name = "client_id", value = "${spring.tokenGen.client}"),
      			@Param(name = "client_secret", value = "fCkXFVEmEK2Qbexg"),
      			@Param(name = "scope", value = "SMS"),
      			@Param(name = "grant_type", value = "client_credentials") })
   public void getUserSubscriptionAPI() {
       ....
       String accessToken = tokenGen.getRespValue(TokenGen.ACCESS_TOKEN);
       ....
   }
}
```

*** example 4:** Different system will reply different json format,

* Facebook: {"access_token":"...", "expires_in":..., "machine_id":"..."}                  
* Twitter: {"token_type":"bearer","access_token":"AAAAAAAAAAAAAAAAAAAAAAAAA%3DAAAAAAAAAAAAAAA"}
             
TokenGen already pre-defined three values: access_token, expires_in, token_type.
             You can define your own response keys(multiple keys support), see example below

```
   @Autowired
   private TokenGen tokenGen;

   @Token(endPoint = "${telstra.sms.api.gateway}", params = {
			@Param(name = "client_id", value = "${spring.tokenGen.client}"),
			@Param(name = "client_secret", value = "fCkXFVEmEK2Qbexg"),
			@Param(name = "scope", value = "SMS"),
			@Param(name = "grant_type", value = "client_credentials") }, respKeys = {"machine_id"})
   public void getUserNameAPI(){
      ....
      String accessToken = tokenGen.getRespValue(TokenGen.ACCESS_TOKEN);
      String machineId = tokenGen.getRespValue("machine_id");
      ....
   }
```

#Any issue or questions
```
Email: davidzheng1022@gmail.com
```