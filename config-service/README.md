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


현재 흐름은 로컬 저장소에 있는 yml 파일을 user-service 에게 제공하는 역할

### 바뀐 yml 파일의 값을 적용하려면
- user-service 서버 재기동
- Actuator refresh (user-service/actuator/refresh)
- spring cloud bus 사용

### rebbitmq 관련
```
brew update
brew install rabbitmq
xcode-select —install
export PATH=$PATH:/usr/local/sbin
rabbitmq-server
```

### 대칭키 관련
- bootstrap 의존성 필요
- bootstrap.yml 파일에 아래와 같은 형식의 키를 추가

#### bootstrap.yml
```
encrypt:
  key: xxx
```

#### 저절로 암복호화 API 생김
`POST 127.0.0.1:8888/encrypt`  
`POST 127.0.0.1:8888/decrypt`

#### native 경로 user-service.yml
{cipher}를 넣으주면 알아서 복호화해줌
```
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb
    username: aaa
    password: '{cipher}2ea08fe362e456b1b3e90eb89877d3e93fb9a8c41606266ddef5ed7177c6fc5d'
```

## 비대칭키 관련 
JDK keytool 이용 일반적으로 private key 로 암호화하고 public key 로 복호화 

1. 원하는 디렉터리 생성하여 key 생성
alias: 사용할 별칭
keyalg: 알고리즘
dn: 서명 정보 (CertificationName, OrganizationUnit, Organization, Location, Country) 
keypass: 키에 액세스할 때 사용할 패스워드
keysotre: 키스토어에 저장될 이름
storepass: 키 저장소에 액세스할 때 사용할 패스워드
```
keytool -genkeypair -alias apiEncryptionKey -keyalg RSA -dname "CN=ggyool, OU=API Development, O=ggyool.co.kr, L=Seoul, C=KR" -keypass "test1234" -keystore apiEncryptionKey.jks -storepass "test1234" 
```

2. 생성된 키 정보 확인
```
keytool -list -keystore apiEncryptionKey.jks -v
```
입력하면 store 비밀번호 입력하면 정보 확인 가능 

3. 공개키를 끄집어 내 파일로 저장
   rfc(Request For Comments): 인터넷에서 쓰이는 표준 형식 
```
keytool -export -alias apiEncryptionKey -keystore apiEncryptionKey.jks -rfc -file trustServer.cer
```
실행 후 파일 보면 공개키를 확인 할 수 있음
   
4. jks 파일로 바꿔 사용할 수도 있음 
`keytool -import -alias trustServer -file trustServer.cer -keystore publicKey.jks`
(reference)[https://cloud.spring.io/spring-cloud-config/reference/html/#_key_management]
   
5. 위와 마찬가지로 -list 명령어 쓰면 키 정보 확인 할 수 있음

6. 대칭키 때와 마찬가지로 /ecrypt 호출하면 대칭키보다 긴 텍스트가 생김 
   그 값으로 user-service.yml 수정 
