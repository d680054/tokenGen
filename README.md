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
* Multiple different token support, smart identifier
------------------------------------
* property placeholder support


# Quick Start
1. Download the code and run 'mvn install'

2. In your project, add the dependency to your project pom file
```
    <dependency>
	<groupId>com.pactera.adm</groupId>
	<artifactId>token-gen</artifactId>
	<version>1.0</version>
    </dependency>
```
3. Help Spring to find it:
   in spring-boot project, add @ComponentScan({"com.pactera.adm"})
   in other spring project, <context:component-scan base-package="com.pactera.adm" />

4. config the annotation before any API call
```
   @Autowired
   private TokenGen tokenGen;

   @TokenGen(endPoint = "https://api.telstra.com/v1/oauth/token", params = {
			@Param(name = "client_id", value = "7HucPuzGLtXSEjOE1b0RAx3zKNnQiUHZ"),
			@Param(name = "client_secret", value = "fCkXFVEmEK2Qbexg"),
			@Param(name = "scope", value = "SMS"),
			@Param(name = "grant_type", value = "client_credentials") })
   public void getUserNameAPI(){
      String access_token = tokenGen.getToken();
      ....
   }
```