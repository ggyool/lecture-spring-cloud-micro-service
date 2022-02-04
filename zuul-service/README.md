#### * API Gateway

클라이언트에서 micro service 로 직접 API를 날리면 변화가 생기면 클라이언트의 코드가 바뀌어야함 <br>
따라서 단일 진입점 역할을 하는 API Gateway 를 둠 (포록시)

- 인증 및 권한 부여
- 서비스 검색 통합
- 응답 캐싱
- 회로 차단기 및 QOS (Quality Of Service) 다시 시도
- 속도 제한
  -부하 분산
- 로깅, 추적, 상관 관계
- 헤더, 쿼리 문자열 및 청구 변환
- IP 허용 목록에 추가


#### * Spring Cloud 에서의 MSA 간 통신
1. RestTemplate
2. Feign Client

#### * Netflix Ribbon: Client side Load Balancer
- 서비스 이름으로 호출
- Health Check
- 비동기 처리 X
- 현재 maintenance 상태

#### * Netflix Zuul
- Routing
- API gateway
- 현재 maintenance 상태


### * 실습
#### zuul-service

1. pom.xml 설정
- zuul을 현재 지원하지 않아서 boot 버전 `2.3.8.RELEASE` 로 수정 (2.3.x 까지 가능)
- maven repository 에서 zuul 의존성 추가하였음
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
    <version>2.2.10.RELEASE</version>
</dependency>
```

2. application.yml 설정
- 라우팅 정보 설정:
localhost:8000/first-service/** 요청은 8081로 간다.
```yml
zuul:
  routes:
    first-service:
      path: /first-service/**
      url: http://localhost:8081
    second-service:
      path: /second-service/**
      url: http://localhost:8082
```
3. main에 `@EnableZuulProxy` 추가

#### first-service 와 second-service 추가
두 서비스 모두 /welcome 이라는 API만 만들어놓음 <br>
first-service 는 8081 포트, second-service 는 8082 포트서 실행 


### * Zuul Filter 추가
요청 uri 를 로깅하는 용도로 ZuulFilter 를 구현 
[ZuulLoggingFilter.java 참고](/src/main/java/org/ggyool/zuulservice/filter/ZuulLoggingFilter.java)
