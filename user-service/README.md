### discoveryservice 의 client 인 user-service
자세한 내용은 discoveryservice 의 README 참고


### FeignClient
- REST call 을 추상화 한 Spring Cloud Netflix 라이브러리
- 사용 방법
    - 호출하려는 HTTP Endpoint 에 대한 Interface 생성
    - `@FeignClient` 선언
- Load balanced 지원

### 코드단
- 의존성 추가
```xml

```
- 어노테이션 추가

- 인터페이스 추가
