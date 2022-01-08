##Tutorial URL 
https://spring.io/guides/gs/batch-processing/

##TODO
  - [X] HSQL 대신 Mysql 사용하기
  - [X] Mybatis 도입 
  - [X] REST API - totalCount를 가져오는 Tasklet Step 작성
  - [X] REST API - Step간 데이터 공유(totalCount,pageNo)
  - [X] REST API - 1 page만 DB에 저장하는 Step 작성 (MybatisBatchWriter 사용)
  - [X] REST API - 전체 페이지를 순회할때까지 Step을 반복하는 workflow 구현하기
  - [X] H2를 이용한 테스트 환경 구축 

##TO-DO LOG 
###HSQL 대신 Mysql 사용하기
- Docker로 MySQL Container 띄우기
  ```
      docker run --name mysql-container ^
      -v C:/Users/<user_name>/mysql-volume/my-conf:/etc/mysql/conf.d ^
      -v C:/Users/<user_name>/mysql-volume/data:/var/lib/mysql ^
      -e MYSQL_ROOT_PASSWORD=<password> -d -p 3306:3306 ^
      mysql:latest
  ```
- 유저 및 DB 생성
  ```
  컨테이너 접속 후 
   mysql -u root -p
   create database batch_db default CHARACTER SET UTF8;
   create user 'batch_user'@'%' identified by 'batch';
   grant all privileges on batch_db.* to batch_user@'%';
  ```
- DB접속정보 암호화
  - jasypt-spring-boot-starter 추가
  - Encrypt Bean 추가

###Mybatis 도입 
- 참고 1 : https://devlog-wjdrbs96.tistory.com/200
- 참고 2 : https://jsonobject.tistory.com/225
- pom 설정
  ```xml
  <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>mybatis-spring-boot-starter</artifactId>
      <version>2.2.1</version>
  </dependency>
  ```
- Mybatis 설정 정보 소스 
  - org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
  - org.mybatis.spring.boot.autoconfigure.MybatisAutoProperties
- application.yml 주요 설정 값들
  ```properties
  #참고 : https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/
  mybatis.config-location=
  mybatis.mapper-locations=
  mybatis.type-aliases-package=
  
  #mybatis.executor-type : SIMPLE,REUSE,BATCH
  #BATCH모드 이해하기 : https://bkim.tistory.com/4
  mybatis.executor-type=BATCH
  
  # mybatis.configuration.*
  # mybatis.config-location과 같이 사용하지 못함. 
  # https://mybatis.org/mybatis-3/configuration.html#settings
  mybatis.configuration.map-underscore-to-camel-case=true
  # 실행가능 JAR로 실행할 경우 설정 : https://do-study.tistory.com/78
  mybatis.configuration.vfsImpl=org.mybatis.spring.boot.autoconfigure.SpringBootVFS
  ```
### REST API - totalCount를 가져오는 Tasklet Step 작성
- Job 설계
  - 방안 1 : Step 당 한 페이지씩 처리 (chunkSize = api의 numOfRows)
  - 방안 2 : DTO를 LIST타입으로 하여 Read 한번에 페이지 하나씩 전달 (chunkSize = 1(한번에 처리할 LIST의 개수) , list Size = api의 numOfRows)
- 방안 1 선택 
  - data의 totalCount를 가져오기 위한 tasklet step 작성
  - 데이터를 저장하기 위한 step 작성(전체 페이지 수에 따라 실행횟수 동적변경 필요)
  - jobExecutionContext를 통해 step간 데이터 공유  
  - Step Listener 사용해서 페이지번호 업데이트
- Tasklet 작성 하기 
  - https://juneyr.dev/2019-07-24/spring-batch-tasklet
- 원하는 batch만 동작시키기
  - https://velog.io/@lxxjn0/Spring-Batch-Guide-04.-Spring-Batch-Job-Flow
  
### REST API - Step간 데이터 공유(totalCount,pageNo)
- Step간 데이터 공유 (ExecutionContextPromotionListener를 사용하는 방법)
  - https://docs.spring.io/spring-batch/docs/current/reference/html/common-patterns.html#passingDataToFutureSteps
  - https://stackoverflow.com/questions/2292667/how-can-we-share-data-between-the-different-steps-of-a-job-in-spring-batch
  
### REST API - 1 page만 DB에 저장하는 Step 작성 (MybatisBatchWriter 사용)
- rest api용 Reader 구현 
  - https://innopc.tistory.com/31
- Chunk 지향 처리
  - Reader와 Processor에서는 1건씩 읽어 지고, Writer에선 Chunk 단위로 처리
  - 참고 : https://jojoldu.tistory.com/331

### REST API - 전체 페이지를 순회할때까지 Step을 반복하는 workflow 구현하기
- decider를 이용한 JobFlow 캡슐화 
  - https://jojoldu.tistory.com/328

###H2를 이용한 테스트 환경 구축
- 테스트 DB로 H2 지정하기 
  - https://taes-k.github.io/2021/04/05/spring-test-isolation-datasource/
  - https://moonsiri.tistory.com/48?category=932632
- @ActiveProfiles("test") : 테스트 실행시 spring profile을 "test"로 지정
- RunWith가 사라진 이유 : @ExtendWith를 @SpringTest가 가지고 있기 때문. 
  - 참고 URL https://www.whiteship.me/springboot-no-more-runwith/
- Spring batch 통합 테스트 작성 
  - https://jojoldu.tistory.com/455
- spring batch 단위 테스트 작성
  - https://jojoldu.tistory.com/456?category=902551

##이슈 발생 및 해결
* mysql로 DB변경시 테이블이 자동생성 되지않는 현상 발생 
  - 기본적으로는 embedded db(H2 HSQL...)를 사용하지 않으면 schema sql 문을 실행시키지 않음. 
    - spring.datasource.initialization-mode의 기본값이 DatabaseInitializationMode.EMBEDDED기 때문 
  - application.yml 설정 
    - spring.datasource.initialization-mode=always => 모든 DB에 대해서 class패스 내 schema-{platform}.sql을 실행한다. 
    - spring.datasource.initialization-mode=never => class패스 내 schema-{platform}.sql을 절대 실행하지 않는다.
    - spring.datasource.platform=mysql => schema-mysql.sql을 실행한다. 지정해 주지 않으면 기본값으로 schema-all.sql을 실행한다.
  - 참고 : https://wan-blog.tistory.com/52

* Spring Batch 메타 테이블이 자동생성되지 않는 현상 
  - spring batch의 경우 따로 설정해주어야 시작 시점에 schema-sql을 실행시킴. 
  - 커스터마이징 설정 방법 찾기
    1. 검색어 "@ConfigurationProperties" 혹은 "@EnableConfigurationProperties"
    2. org.springframework.boot.autoconfigure.batch.BatchProperties 발견
    3. jdbc의 schema, platform, tablePrefix, initializeSchema 등을 설정할 수 있음.
  - spring.batch.jdbc.initialize-schema: always 추가

* MySQL에서 테이블명 인식하지 못하는 현상
  - MySQL설치시 대소문자를 구분하도록 되어 있는 경우가 있다.(lower_case_table_names = 0 인경우) 
  - 컨테이너 재생성해서 해결했음. 
    - mysql 컨테이너 설정 방법 참고 URL : https://hub.docker.com/_/mysql?tab=description
    - 컨테이너 생성옵션 추가 -v C:/Users/user/mysql-volume/my-conf:/etc/mysql/conf.d
    - C:/Users/user/mysql-volume/my-conf/myconfig.cnf 내용 
      ```
      lower_case_table_names=1
      ```

* Map 을 DTO로 변환해야하는 상황 발생 
  - https://m.blog.naver.com/PostView.naver?isHttpsRedirect=true&blogId=beabeak&logNo=220112193901
  - Apache Commons BeanUtils.populate 사용
  
## 설정정보들
####Spring Boot 설정(공식 사이트)
- https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html

#### Oracle VS MySQL
+ Oracle 단독 
  - ojdbc10은 driverClassName이 ojdbc6와 다름
+ ID 생성
  - Oracle : 따로 SEQUNCE를 만들어서 NEXTVAL로 사용
  - MySQL : 테이블 생성시 AUTOINCREMENT로 선언
+ MERGE
  - Oracle : MERGE문 사용
  - MySQL : INSERT문 하단에 ON DUPLICATE KEY UPDATE사용 

#### Batch, Quartz 연동
https://hyejikim.tistory.com/67
https://examples.javacodegeeks.com/enterprise-java/spring/batch/quartz-spring-batch-example/