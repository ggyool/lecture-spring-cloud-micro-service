### 공통 설정을 관리하는 Config 서버
1. 의존성 추가
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```
2. 어노테이션 추가 
```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication {
    // ...
}
```


현재 흐름은
로컬 저장소에 있는 yml 파일을 user-service 에게 제공하는 역할

바뀐 yml 파일의 값을 적용하려면
- user-service 서버 재기동 ()
- Actuator refresh (user-service/actuator/refresh)
- spring cloud bus 사용

