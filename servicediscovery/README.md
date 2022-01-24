
### Service Discovery
서비스의 위치를 등록하고 검색하는 서비스 (주소록과 유사)
그 중 Spring Cloud Netflix Eureka를 사용

#### discoveryservice
- dependency 추가 (spring-cloud-starter-netflix-eureka-server) 
- main에 @EnableEurekaServer 추가
- application.yml에 적절한 설정 추가

#### user-service
- dependency 추가 (spring-cloud-starter-netflix-eureka-client, spring-boot-starter-web)
- main에 @EnableDiscoveryClient 추가
- application.yml에 적절한 설정 추가

#### 서비스 등록 및 확인

1. eureka 서버 실행 (8761 포트)
2. user-service 등록 (9001, 9002, 9003, 9004 포트)
- 9001: 인텔리제이 일반 실행 (yml 파일의 9001 설정 사용)
- 9002: 인텔리제이에의 실행환경설정에 -Dserver.port=9002 로 실행
- 9003: spring-boot:run 으로 실행 <br> 
```mvn spring-boot:run -Dspring-boot.run.jvmArguments='-Dserver.port=9003'```
- 9004: jar 만들어서 실행
```
mvn clean (이미 빌드 결과가 있을 경우 target 폴더가 있는데 삭제)
mvn complie package (로그에 jar위치 나옴 Building jar: /Users/ggyool/Practice/online-lecture-microservice/user-service/target/userservice-0.0.1-SNAPSHOT.jar)
java -jar -Dserver.port=9004 ./target/userservice-0.0.1-SNAPSHOT.jar (실행)
``` 
3. 127.0.0.1:8671에서 인스턴스 4개 UP 확인

#### 랜덤 포트를 활용하여 인스턴스 등록

- 문제 상황 <br>
application.yml 파일에 server.port=0 으로 설정하면 랜덤 포트를 사용함 <br>
이 설정으로 여러번 실행시키면 다른 포트로 실행되는 것을 확인할 수 있음 <br>
하지만 127.0.0.1:8671에서 확인하면 하나의 인스턴스만 등록되어 있음 <br>
이는 유레카서버에 기본적으로 ip:application-name:port로 등록되는데 (ex. 192.168.219.101:user-service:0) <br>
실행중인 포트가 아니라 server.port 값을 이용하여 port값에 사용해서 이런 문제가 생김 <br>

- 해결 <br>
아래와 같은 옵션으로 실행시켜 다른 instance-id를 가져가게 함 (random.value는 32바이트 문자열) <br>
```yml
eureka:
  instance:
    instance-id: ${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${random.value}}
```
