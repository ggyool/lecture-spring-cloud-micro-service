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

5. Global Filter 사용 (라우팅 마다 다른 기능이 아닌 공통된 기능이 필요할 때 사용하면 편함)
- GlobalFilter.java 추가

- application.yml 에 default-filters 추가 
```yaml
spring:
  application:
    name: apigateway-service
  cloud:
    gateway:
      default-filters:
        - name:
            args:
              baseMessage: Spring Cloud Gateway Global Filter
              preLogger: true
              postLogger: true
```

6. Logging Filter 추가 
- 위의 Custom Filter 를 만드는 거지만
- OrderedGatewayFilter 를 만들어서 필터의 순서 지정 (델리게이트 패턴)

7. eureka 와 함께 사용
- localhost:8081 같은 주소의 라우팅 정보를 직접 등록하는 것이 아닌 eureka 에서 마이크로 서비스 이름으로 알아냄  

- eureka 서버에 등록 (api-gateway, first-service, second-service)
```yaml
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8761/eureka
```
- application.yml 의 uri 부분 수정
```yaml
routes:
    - id: first-service
      uri: lb://MY-FIRST-SERVICE
      ...
```

8. Load Balancer 로 실행
- first service 랜덤 포트로 2개 실행
- second service 랜덤 포트로 2개 실행
- 실행해 보면 같은 요청이라도 라운드로빈으로 각각 인스턴스가 한 번씩 처리함 
