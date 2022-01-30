### Spring Cloud Gateway 사용
- zuul을 대체하는 API Gateway 
- 비동기 방식 지원
- tomcat 이 아닌 netty 서버로 실행된다

1. 의존성 추가 
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

2. application.yml 추가
- 유저가 localhost:8000/first-service/xxx 으로 요청하면 localhost:8081/first-service/xxx 으로 라우팅 됨
- 이 떄, api gateway의 filter 에서 request, response header 에 값을 추가함 


```yml
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      routes:
        - id: first-service
          uri: http://localhost:8081/
          predicates:
            - Path=/first-service/**
          filters:
            - AddRequestHeader=first-request, first-request-header2
            - AddResponseHeader=first-response, first-response-header2
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
            - AddRequestHeader=second-request, second-request-header2
            - AddResponseHeader=second-response, second-response-header2
```

3. 2번과 같은 작업을 yml 파일이 아닌 RouteLocator 등록하여 설정
- FilterConfig.java 에서 RouteLocator 빈으로 등록

4. AbstractGatewayFilterFactory 를 extends 하여 Custom Filter 구현
- CustomFilter.java 추가
    - AbstractGatewayFilterFactory extends
    - Config 내부 클래스 추가 
    - default 생성자 추가 (super 호출)
    - apply(Config config) 메서드 오버라이드


- application.yml 또는 RouterLocator 의 Filters 수정
```yaml
filters:
            - CustomFilter
#            - AddRequestHeader=first-request, first-request-header2
#            - AddResponseHeader=first-response, first-response-header2
```
```java
.filters(
                                        f -> f.filter(customFilter.apply(new CustomFilter.Config()))
//                                        f -> f.addRequestHeader("first-request", "first-request-header")
//                                                .addResponseHeader("first-response", "first-response-header")
                                )
```


