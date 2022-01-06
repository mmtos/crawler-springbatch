##Tutorial URL 
https://spring.io/guides/gs/batch-processing/

##TODO
  - [X] HSQL 대신 Mysql 사용하기
  - [X] Mybatis 도입 
    - 참고 1 : https://devlog-wjdrbs96.tistory.com/200
    - 참고 2 : https://jsonobject.tistory.com/225
  - [X] MybatisBatchWriter 사용하기
  - [X] Step간 데이터 공유
  - [ ]
  - [ ] REST API 데이터 저장 예시 작성 
  - [ ] 실행가능 JAR로 실행해보기

##테스트 환경
H2를 이용한 테스트 환경 : https://taes-k.github.io/2021/04/05/spring-test-isolation-datasource/
테스트 실행시 profile을 test로 지정하는 방법 : 클래스 Annotation @ActiveProfiles("test")
RunWith 어디로? : https://www.whiteship.me/springboot-no-more-runwith/
spring batch 통합 테스트 https://jojoldu.tistory.com/455
spring batch 단위 테스트 https://jojoldu.tistory.com/456?category=902551
spring batch 테스트 : https://moonsiri.tistory.com/48?category=932632

##Spring Batch 사용법
Tasklet 작성 : https://juneyr.dev/2019-07-24/spring-batch-tasklet
원하는 batch만 동작시키기 : https://velog.io/@lxxjn0/Spring-Batch-Guide-04.-Spring-Batch-Job-Flow
Step간 데이터 공유 : 
  https://docs.spring.io/spring-batch/docs/current/reference/html/common-patterns.html#passingDataToFutureSteps
  https://stackoverflow.com/questions/2292667/how-can-we-share-data-between-the-different-steps-of-a-job-in-spring-batch
job내부 step 흐름제어 : https://velog.io/@lxxjn0/Spring-Batch-Guide-04.-Spring-Batch-Job-Flow

##문제 발생 및 해결
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
## 설정정보들
#### 공식 사이트
- https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html

#### MyBatis
* pom 설정 
  ```xml
  <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>mybatis-spring-boot-starter</artifactId>
      <version>2.2.1</version>
  </dependency>
  ```
* 설정파일
  ```text
  org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
  org.mybatis.spring.boot.autoconfigure.MybatisAutoProperties
  ```
* 주요 설정 값들(https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
  ```properties
  mybatis.config-location=
  mybatis.mapper-locations=
  mybatis.type-aliases-package=
  mybatis.executor-type=SIMPLE
  #mybatis.executor-type=REUSE
  # BATCH모드 이해하기 https://bkim.tistory.com/4
  #mybatis.executor-type=BATCH
  
  # mybatis.configuration.* (mybatis.config-location과 같이 사용하지 못함.)
  # https://mybatis.org/mybatis-3/configuration.html#settings
  mybatis.configuration.map-underscore-to-camel-case=true
  # 실행가능 JAR로 실행할 경우. https://do-study.tistory.com/78
  mybatis.configuration.vfsImpl=org.mybatis.spring.boot.autoconfigure.SpringBootVFS
  ```

#### Oracle
ojdbc10은 driverClassName이 ojdbc6와 다름

#### Batch, Quartz 연동
https://hyejikim.tistory.com/67
https://examples.javacodegeeks.com/enterprise-java/spring/batch/quartz-spring-batch-example/

