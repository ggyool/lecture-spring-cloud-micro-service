### discoveryservice 의 client 인 user-service
자세한 내용은 discoveryservice 의 README 참고


### FeignClient
- REST call 을 추상화 한 Spring Cloud Netflix 라이브러리
- 사용 방법
    - 호출하려는 HTTP Endpoint 에 대한 Interface 생성
    - `@FeignClient` 선언
- Load balanced 지원

### 코드단
1. 의존성 추가
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```
2. Application 실행부에 어노테이션 추가
`@EnableFeignClients`
3. 인터페이스 추가
```java
@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrdersByUserId(@PathVariable String userId);
}
```

### FeignClient 로깅 사용
1. application.yml 에 추가
```yaml
logging:
  level:
    com.ggyool.userservice.client: DEBUG
```

2. Application 실행부에 bean 추가
```java
@Bean
public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
}
```

3. 평소처럼 Slf4j 추가하고 로깅하면 됨

### FeignClient 예외 처리
- 각각 try catch 해도 되지만 ErrorDecoder 구현하여 전역적으로 관리해도 됨
1. ErrorDecode 인터페이스 구현 
```java
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                break;
            case 404:
                if (methodKey.contains("getOrdersByUserId")) {
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()), "User's orders is empty");
                }
                break;
            default:
                return new Exception(response.reason());
        }
        return null;
    }
}
```
2. bean 추가
```java
@Bean
public FeignErrorDecoder feignErrorDecoder() {
    return new FeignErrorDecoder();
}
```
3. 등록하고 4xx, 5xx 발생하면 알아서 ErrorDecoder 실행됨

4. Client 마다 별도의 ErrorDecoder 도 사용 가능
```java
@FeignClient(name="catalog-service", configuration = FeignErrorDecoder2.class)
public interface CatalogServiceClient {
```

### 인스턴스 여러개일 때 DB 동기화 문제
1. 하나의 DB 사용
2. 인스턴스가 각각 DB에 저장하 Messaging Queuing Server 에 알려줌, 구독한 쪽에서는 이를 알고 데이터 동기화
3. 인스턴스가 저장하지 않고 Messaging Queuing Server 에 메시지를 보내고 Queuing Server 가 단일 DB에 저장 (DB 저장 위임)


### Apache Kafka
- Apache Software Foundation 의 OpenSource Message Broker Project (Scalar 언어 사용)
- 링크드인에서 개발, 2011년 오픈 소스화
- 2014년 kafka 개발에 집중하기 위해 Confluent 라는 회사 설립
- 실시간 데이터 피드를 관리하기 위해 통일된 높은 처리량, 낮은 지연 시간을 가짐


### kafka 이전에는?
- End-to-End 연결 방식의 아키텍쳐
- 데이터 연동의 복잡성 증가
- 서로 다른 데이터 Pipeline 연결 구조
- 확장이 어려운 구조

### kafka 이후
- 모든 시스템으로 데이터를 실시간으로 전송하여 처리할 수 있는 시스템, 데이터가 많아지더라도 확장이 용이한 시스템
- Producer / Consumer 분리
- 메시지를 여러 Consumer 에게 전달 가능
- 높은 처리량을 위한 메시지 최적화
- scale-out 가능
- Eco-system

### kafka Broker
- 실행 된 kafka 어플리케이션 서버
- 일반적으로 3대 이상의 Broker Cluster 구성
- Zookeeper 연동 (브로커를 관리 - coordinator)
  - 역할: 메타데이터 (Broker ID, Controller ID 등) 저장
  - Controller 정보 저장
- n 개의 Broker 중 1대는 Controller 기능 수행 (리더 역할)
  - 각 Broker 에게 담당 파티션 할당 수행 
  - Broker 정상 동작 모니터링 관리
  
### kafka 설치
- kafka.apache.org 에서 download -> tar 압축 해제
- ./config 경로 - 다양한 설정 파일 (ex. zookeeper.properties, server.properties etc..)
- ./bin 경로 - 각종 실행 스크립트 파일 (ex. zookeeper-server-start.sh, kafka-server-start.sh etc..)
- ./bin/windows 경로 - 윈도우 스크립트 파일 (**.bat)

### kafka client
- kafka 와 데이터를 주고받기 위해 사용하는 Java Library
- Producer, Consumer, Admin, Stream 등 kafka 관련 API 제공
- 다양한 3rd party library 존재: C/C++, Node.js, Python .NET 등 (cwiki.apache.org/confluence/display/KAFKA/Clients)

### Zookeeper 및 kafka 서버 구동
- $KAFKA_HOME/bin/zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties
- $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties

### Topic 생성
- $KAFKA_HOME/bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092 --partitions 1

### Topic 목록 확인
- $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

### Topic 정보 자세한 확인
- $KAFKA_HOME/bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092

### 메시지 생산 예시 스크립트 존재
- $KAFKA_HOME/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic quickstart-events

### 메시지 소비 예시 스크립트 존재
- $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic quickstart-events --from-beginning (처음 부터 받아오는 옵션)

### Kafka Connect
- Data 를 자유롭게 Import/Export 가능
- 코드 없이 Configuration 으로 데이터를 이동
- Standalone mode, Distribution mode 지원
  - RESTful API 통해 지원
  - Stream 또는 Batch 형태로 데이터 전송 가능
  - 커스텀 Connector 를 통한 다양한 Plugin 제공 (File, S3, Hive, MySql etc ...)
- 흐름: 데이터를 보내는 쪽 - Source / 받는 쪽 - Sink
`Source System (Hive, Jdbc...) --> Kafka Connect Source --> Kafka Cluster --> Kafka Connect Sink --> Target System (S3...)`

### kafka Connect 설치
- curl -O http://packages.confluent.io/archive/6.1/confluent-community-6.1.0.tar.gz
- tar xvf confluent-community-6.1.0.tar.gz
- cd $KAFKA_CONNECT_HOME

### Kafka Connect 실행 (8083 port)
- ./bin/connect-distributed ./etc/kafka/connect-distributed.properties

### Topic 목록 확인
- ./bin/kafka-topics --bootstrap-server localhost:9092 --list
- 추가로 4가지 Topic 이 생겨있음
```
__consumer_offsets
connect-configs
connect-offsets
connect-status
```

### JDBC Connector 설치 
1. docs.confluent.io/5.5.1/connect/kafka-connect-jdbc/index.html 접속 후 zip 다운로드
2. etc/kafka/connect-distributed.properties 파일 마지막에 아래 plugin 정보 추가  
사용하려는 파일이 lib 안에 들어있음 (kafka-connect-jdbc-xx.xx.x)  
```
plugin.path=[confluentinc-kafka-connect-jdbc-10.0.1 폴더/lib]
(ex. plugin.path=/Users/ggyool/Desktop/confluentinc-kafka-connect-jdbc-10.5.0/lib)
```
3. JdbcSourceConnector 에서 MariaDB 사용하기 위해 mariadb 드라이버 복사  
```
./share/java/kafka/ 폴더에 mariadb-java-client-2.7.2.jar 파일 복사
해당 파일은 maven dependency 추가하면 생기는 .m2/repository/org/mariadb/jdbc/mariadb-java-client/2.7.2 에 있음
```

### Kafka Source Connect 추가
- 테스트시에는 Postman 사용 아래는 curl 예시
- curl 사용하여 등록
```shell
echo '
{
  "name" : "my-source-connect", // connect 이름
  "config" :{
    "connector.class" : "io.confluent.connect.jdbc.JdbcSourceConnector", // connect 종류
    "connection.url" : "jdbc:mysql://localhost:13306/mydb",
    "connection.user" : "root",
    "connection.password" : "root",
    "mode" : "incrementing", // 자동으로 증가시키는 모드
    "incrementing.column.name" : "id", // 컬럼
    "table.whitelist" : "users", // 변경 사항을 감지할 테이블
    "topic.prefix" : "my_topic_", // prefix
    "tasks.max" : "1"
  } 
}
' | curl -X POST -d @- http://localhost:8083/connectors --header "Content-Type:application/json"
```  
- curl 사용하여 목록 확인
`curl http://localhost:8083/connectors | jq`
- kafka connect 확인 
`curl http://localhost:8083/connectors/my-source-connect/status | jq`
  
### 테스트 흐름
1. kafka connect 에 POST 로 등록
2. mariadb users 테이블에 insert
3. 생긴 토픽 확인
```shell
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic my_topic_users --from-beginning
```
결과
```json
{
  "schema":
  {
    "type":"struct",
    "fields":
    [
      {"type":"int32","optional":false,"field":"id"},
      {"type":"string","optional":true,"field":"user_id"},
      {"type":"string","optional":true,"field":"pswd"},
      {"type":"string","optional":true,"field":"name"},
      {"type":"int64","optional":true,"name":"org.apache.kafka.connect.data.Timestamp","version":1,"field":"created_at"}
    ],
    "optional":false,
    "name":"users"
  },
  "payload":{"id":1,"user_id":"user1","pswd":"test1111","name":"username1","created_at":1654642148000}
}
```

### Kafka Sink Connect 사용
- POST /connectors 에 sink connect 등록
```json
{
  "name":"my-sink-connect",
  "config":{
    "connector.class":"io.confluent.connect.jdbc.JdbcSinkConnector",
    "connection.url":"jdbc:mysql://localhost:13306/mydb",
    "connection.user":"root",
    "connection.password":"root",
    "auto.create":"true",
    "auto.evolve":"true",
    "delete.enabled":"false",
    "tasks.max":"1",
    "topics":"my_topic_users"
  } 
}
```

1. my_topic_users 라는 테이블이 생성되어 있고 토픽의 기존 데이터도 들어가 있음  
2. 새로운 데이터 insert 실행
3. my_topic_users 에도 생성됨 

