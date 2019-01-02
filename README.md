# Introduction

This is a spring-boot starter project to simplify generating access token based on OAuth2 standards supporting two grant types: Client Credentials and Refresh Token.
An interceptor which is used to generate/refresh the access token will be automatically added into the RestTemplate object.


# Just Two Steps

Step1: Add the dependency to your pom file
```
   <dependency>
   	<groupId>com.hj</groupId>
   	<artifactId>tokengen-spring-boot-starter</artifactId>
   	<version>2.0</version>
   </dependency>
```
Step2:  Add the entries into your property file

```
   tokenGen.url=https://xxxx the oauth2 server url
   tokenGen.clientId=your client id
   tokenGen.clientSecret=your client secret
   tokenGen.scope=scope
```

## Ref

https://www.oauth.com/oauth2-servers/access-tokens/client-credentials/ 


#Any issue or questions
```
Email: davidzheng1022@gmail.com
```