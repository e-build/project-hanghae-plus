# 개요
- 이커머스 도메인 전반 백엔드 설계
  - 구매자, 판매자, 상점, 상품, 주문, 결제, 장바구니, 즐겨찾기
  - [요구사항 상세](docs/commerce-scenario.md)

# 기술스택
- Application
  - Spring boot 3
  - Kotlin, JPA
  - Mysql 8, Redis
  - Resilience4J
- Testing
  - Junit5, Jacoco, Ktlint, Testcontainer, MockK
  - K6, InfluxDB, Grafana
- CI/CD
  - Github Action, AWS ECR, AWS ECS
- Log & Monitoring
  - Logback, AWS CloudWatch, AWS Lambda

# 아키텍처
### 프로젝트 모듈 구성
- commerce-api
  - 웹 인터페이스 + 도메인 모듈
- commerce-infra
  - 의존하는 외부 시스템 구현체 모음
  - :db-main - JPA Entity, JpaRepository 구현체 등
  - :external-api - 써드파티 클라이언트 
  - :redis-main - redis 접근 구현체
- commerce-support
  - :logging - logback
  - :monitoring
  
### API 모듈 패키지 구성
- Api - 사용자에게 정보를 표시하고 사용자가 내린 명령을 수행하는 등의 상호작용 수행
- Application - 표현 영역과 도메인 영역을 연결하는 창구 역할. 주로 도메인 개체 간 흐름 제어
- Domain - 비즈니스 정책 및 규칙 담당
- Infrastructure - 애플리케이션이 의존하는 외부 시스템(DB, Redis, 외부 API, ...)에 대한 인터페이스를 제공

# 프로젝트 이후 추가 고민
### Github Action
- PR 시, ktlint의 피드백이 라인에 노출됨으로 인해 코드 가독성을 해쳐 코드를 보는 것이 어려워짐.
  - ScaCap/action-ktlint 의 불편함 
    - reporter 속성으로 `github-pr-review`, `github-pr-check` 를 지정할 수 있는데 둘 다 결국 PR에서 변경된 Line 별로 ktlint 의 피드백이 생성됨
    - 리뷰 시 라인에 lint 피드백이 달려서 코드 가독성을 심각하게 낮춘다고 판단함.
    - .editorconfig 설정대로 github action 에서의 lint 체크 수행을 위해선 별도 설정 필요
  - gradle task 로 직접 작성
    - `./gradlew ktlintCheck`
      - ktlint 피드백은 github action의 job detail을 통해 확인. gradle task 로 lint 체크 시, PR 페이지에서 못보고 action 탭으로 한단계 더 들어가야 한다는 불편함이 있음
      - ktlint 검사로 인해 PR Check가 실패하는 경우, 사실 한개라도 발생하면 로컬에서 다시 수정하여 PR 해보게 된다는 점에서 ktlint 검증 실패가 코드의 어떤 라인에서 발생했는 지 보다, 발생했는지 안했는 지 여부가 더 중요하다고 판단함.  
      > 로컬에서 commit 이전에 ktlintCheck 자동 수행으로 확인해볼 수 있도록 `./gradlew addKtlintCheckGitPreCommitHook` 활용 가능
- CI 에서 unitTest와, integrationTest 를 분리하여 구성하였을 때 얻을 수 있는 장단점은 무엇인지 
  - Mike Cohn이 처음으로 정의한 테스트 피라미드의 하단에는 유닛 테스트, 중간에는 서비스 테스트, 상단에는 UI 테스트가 표시됨. 이름은 다소 부정확할 수 있지만 그 전제는 합리적으로 보임.
    > 1. 빠르고 쉽게 실행 가능한 자동화된 유닛 테스트로 강력한 기반을 다지고 
    > 2. 작성하기 더욱 복잡하며 실행 시간도 더 오래 소요되는 테스트로 나아간 후 --> 통합 테스트
    > 3. 마지막에 소수의 가장 복잡한 테스트로 완료하는 것. --> E2E 테스트, 성능 테스트, UI 테스트
   - 타 컴포넌트와 의존하지 않음으로써 단위테스트가 가지는 큰 장점인 빠른 피드백을 활용할 수 있음
   - 통합테스트 실행 시 필요한 외부 컴포넌트들을 사전 구성하는 작업은 CI 과정에서 꽤 큰 시간을 소요할 수 있음. 
     - ex) 테스트 DB(with 시드 데이터), 테스트 Redis  
   - 속도 측면에서의 장점과 별개로 고민해보면, 단위 테스트가 애플리케이션의 뼈대를 검증하는 작업을 수행한다면, 통합 테스트는 각각의 뼈가 잘 연결되어있는 지 확인하는 역할. 
     둘 간의 책임을 엄격히 분리함으로써 이후 작성되는 테스트들의 목적이 명확해지고, 이는 협업 상황에서 테스트를 다루는 방향성을 제시함
   - 결론 
     - 장점 - 단위 테스트의 빠른 속도를 활용한 CI 상황에서의 빠른 검증
     - 단점 - 개발 초기 단계에서 단위 테스트와 통합 테스트를 별도 구성하여 각각 관리해야 하는 비용 발생
- 운영배포 시 흔히 git tag를 활용하는 데, 수동으로 입력할 때 태그명을 잘못 입력하게 된다면? 휴먼 에러를 줄일 수 있는 배포 구성은?
  - TBU
### Testing
- 단위 테스트의 '단위'란 무엇일까
  - 함수형 언어로 작업하는 경우 단위는 단일 함수일 가능성이 높음. 단위 테스트는 다양한 매개변수를 사용하여 함수를 호출하고 예상 값을 반환하는지 확인함.
  - 객체 지향 언어에서 단위의 범위는 단일 메서드부터 전체 클래스까지 다양할 수 있음. 해당 객체가 가진 상태안에서 어떻게 행동하는 지 검증하는 것이 대부분의 단위 테스트 케이스의 목적.
  - 중요한건 해당 객체가 타 객체와 협력속에서 어떻게 동작하는 지 검증하는 것이 아니라, 순수하게 테스트 대상 객체의 책임만을 검증하는 것.
  - 결론 - 테스트 대상이 되는 객체들이 수행해야하는 책임의 범위 
  - 참고 - [The Practical Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html#WhatsAUnit)
- 단위, 통합, E2E 테스트는 각각 어느 상황에 작성해야 할 지
  - 단위 테스트는 **비즈니스 규칙이 정의되어 있는 도메인 로직을 꼼꼼하게 검증**해야 할 때 활용. 다른 컴포넌트와의 상호작용이 중요한 로직이라면 단위 테스트로는 불가능한 상황 발생. 
    다만 비즈니스 규칙 검증에 있어서 타 컴포넌트와의 상호작용이 불가피하여 단위 테스트가 어려운 상황이라면, 설계에 대해 다시 고민해 볼 필요가 있음. 
  - 통합 테스트는 상세한 비즈니스 규칙의 케이스별 검증 자체보다, 보다 넓은 범위에서 **컴포넌트 간 통합된 상호작용이 의도한대로 동작하는 지 확인할 필요가 있을 때** 활용. 트랜잭션이나 성능, 안정성과 같은 비기능적 요구사항을 검증할 때도 활용
  - E2E 테스트는 HTTP API 요청부터 응답까지의 흐름을 검증할 때 활용.
    - 유닛 테스트나 통합 테스트는 모듈의 무결성을 증명할 수 있는 강력한 도구이지만, 모듈의 무결성이 애플리케이션 동작의 무결성까지는 증명해 줄 수는 없음. 
    - E2E 테스트가 제대로 된 의미를 갖기 위해서는 모든 과정이 실제 환경과 동일해야 한다고 생각함. Mock 데이터를 사용하면 개발자가 원하는 시나리오를 쉽게 테스트할 수 있지만, 
      이는 다시 말하면 Mock 데이터로 만들어진 시나리오는 항상 개발자가 원하는 대로 실행되는 시나리오라는 말이 됨.
    - 실제 환경과 유사한 구성을 준비해야하는 만큼 해당 API 의 다양한 케이스를 검증하는 작업은 큰 시간이 소요됨. 
      차라리 E2E 테스트로 여러 케이스들을 커버하려는 시도보다 해당 **API의 정상 케이스에 대한 하나의 검증**이 현업에서 현실성있는 테스트 방식이지 않을까 생각함.  
- 테스트 간 동일한 코드에 대한 검증이 중복되는 것을 최소화 하도록 고려해야할 지
  - 이미 검증된 코드를 또 검증하는 테스트 케이스를 작성하는 건 개발 및 관리 차원에서 비용일 수 있겠다는 생각이 들었음. 중복된 검증이 발생하지 않도록 단위, 통합, E2E 테스트 간 작성 방식에 대한 고려는 필요하다고 생각함
  - 상대적으로 테스트의 범위가 넒은 테스트(통합, E2E)에서는 테스트 더블 사용을 지양하다 보니, 테스트 케이스 간 동일한 프로덕션 코드에 대한 검증이 중복되는 경우가 많음. 커버 범위가 넓은 테스트의 용도를 제한할 필요가 있다는 것.  
  - 최소화를 목적으로 한다면 하나의 기능을 이루는 모든 계층별 컴포넌트들을 단위 테스트로 작성하는 것이 맞겠지만, 이는 과도한 Mocking, Stubbing 으로 인해 테스트의 범위가 넓어지는 통합, E2E 테스트를 작성하는 것보다 더 많은 비용이 들어갈 수 있음.
  - 결국 바로 위 질문에서 언급한 것과 동일한 방향으로 결론. 많아야 기능당 정상케이스로 1~2개 작성되는 E2E, 통합 테스트로 인해 중복되는 검증은 굳이 최소화를 고려하지 않음. 비즈니스 규칙에 대해 세세하게 작성되는 단위 테스트는 중복되지 않도록 고려해볼만 함 
- 외부 시스템을 연동할 때 테스트 코드를 어떻게 활용하는 게 좋을 지
  - 단위 테스트 코드를 통해 빠르게 피드백 받으며 외부 시스템 연동에 필요한 요청, 응답별 테스트 케이스들을 손쉽게 정리할 수 있음.
  - 문제는 샌드박스, 테스트 환경을 온전히 제공하지 않는 외부 시스템인 경우 CI/CD의 테스트 phase에서 외부 시스템 호출에 따른 비용이 발생할 수 있고, 테스트 코드 실행으로 인한 약속되지 않은 호출 자체가 불가능할 수 있음.
  - 샌드박스, 테스트 환경을 제공한다고 하더라도, 외부 요인에 의해 테스트 케이스의 성공/실패가 결정될 여지를 남기게 됨. 
  - API 연동 및 구축 단계에서 작성된 테스트 케이스는 이후 유지보수나 장애 발생시 활용 가능함으로 보존할 필요가 있음. 해당 TC들은 Disable 처리 후, 정상 동작 검증은 기능 테스트나 QA 로 대체. 
- 아키텍처를 테스트하고 싶을 때 사용할 수 있는 도구도 있을 지
  - 레이어드 아키텍처는 위상을 엄격하게 지키는 것을 권장함. presentation -> business -> persistence 와 같이 레이어 간 의존 방향을 제시하고, 레이어를 건너띄어서 의존하는 것도 지양함
  - 아키텍처의 이러한 제약사항들을 테스트할 수 있는 도구 탐색 - [ArchUnit](https://www.archunit.org/)
- TDD 과정 중 테스트 코드와 어느 정도 완성된 실제 코드가 함께 있는 경우가 많음. 해당 상황에서 테스트 코드와 프로덕션 코드의 커밋 순서나 커밋 메세지 작성 방식은 어떻게 하는 것이 좋을 지
  - 테스트 코드는 별도 커밋 없이, TDD를 통해 완성된 최소한의 기능 단위와 함께 커밋. 테스트 코드가 완성된 실제 코드를 검증하는 것이기 때문에 테스트 코드가 완성된 프로덕션 코드에 포함되어 함께 기록되는 것은 TDD 맥락안에서 자연스럽다고 생각함.
  - 따라서 커밋 메세지 또한 테스트에 대한 별도 언급없이, 추가되는 기능 단위에 대한 설명만을 작성.
- 테스트 상황 설정을 위해 더미 데이터 모델 객체를 정의할 때, 일일이 데이터를 세팅하는 것 말고 좀 더 편리한 방법은 없을 지
  - Easy Random, Fixture Monkey, Instancio, ...
### Logging & Monitoring
- 이슈 추적을 위해 로깅되어야할 정보들 고민
  - 웹 요청
    - 요청 ID: 각각의 웹 요청이나 작업에 대한 고유 식별자
    - 세션 ID: 사용자 세션 식별자
    - 사용자 정보: 현재 사용자 ID
        - ex) [ApiTrackingLoggingAspect.kt](commerce-support/logging/src/main/kotlin/com/hanghae/commerce/logging/aop/ApiTrackingLoggingAspect.kt)
    - 비동기 메서드를 호출하는 등 요청 스레드 외에 스레드를 추가적으로 동작시킬 때, MDC context 를 공유하기 위한 작업 필요
        - ex) [TaskExecutorConfig.kt](commerce-api/src/main/kotlin/com/hanghae/commerce/common/async/TaskExecutorConfig.kt), [MDCCopyTaskDecorator.kt](commerce-support/logging/src/main/kotlin/com/hanghae/commerce/logging/MDCCopyTaskDecorator.kt)
        - [우아한 형제들 기술블로그](https://techblog.woowahan.com/13429/) 참고
  - 트랜잭션 
    - 트랜잭션 ID: 트랜잭션 처리에 대한 고유 식별자
    - 개발 상황에서 트랜잭션 간의 계층구조, 격리수준, 전파수준에 대한 정보도 참고할 수 있도록 구성 가능
    - ex) [TransactionLoggingAspect.kt](commerce-support/logging/src/main/kotlin/com/hanghae/commerce/logging/aop/TransactionLoggingAspect.kt) (구현중)
  - 구현 고민
    - [MDC(Mapped Diagnostic Context)](https://velog.io/@bonjugi/MDC-%EC%9D%98-%EB%8F%99%EC%9E%91%EB%B0%A9%EC%8B%9D-ThreadLocal) 활용 
    - Spring MVC 안에서 로깅 구조를 설계할 때 대부분 특정 메서드 실행 전/후로 로그 구성 작업이 많이 이루어짐. Filter, Interceptor, AOP 등을 활용하여 MDC에 필요한 정보 세팅 가능
    - 어떤 방식으로 하던 하나의 기술로 일관성을 맞추는 것이 사이드 이펙트를 방지하고 트러블슈팅을 용이하게 함. Filter면 Filter만 쓰고, Interceptor Interceptor만 쓰는 것. 
      예를들어 API 성능 로깅은 Interceptor 에서 하고, 웹 요청 로깅을 위해 MDC 셋팅은 AOP 에서 했다고 하면, AOP 실행 이후 MDC는 clear 해야 하기 때문에 Interceptor에서 찍는 로그는 MDC 적용이 안됨
    - Filter와 Interceptor 는 실행 시점에 차이가 있지만 일반적으로 특정 UrlPattern 에 따라 공통적으로 적용할 로직을 정의하는 용도로 사용됨(ex. 인증, 인코딩, 보안, 로깅 등)
    - 트랜잭션과 같이 특정 스프링 빈에 대한 호출을 가로채는 작업은 좀 더 구체적인 상황과 기술에 대한 로깅을 위해 AOP 로 구현하는 것이 편리하다고 생각
- HTTP API 성능 로깅
  - ex) [ApiPerformanceLoggingAspect.kt](commerce-support/logging/src/main/kotlin/com/hanghae/commerce/logging/aop/ApiPerformanceLoggingAspect.kt)
- 슬로우 쿼리 로깅
  - 방법 1. hibernate session 이벤트 설정
    ```properties
    hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS=1000
    org.hibernate.SQL_SLOW=info
    ```
  - 방법 2. hibernate statistics 설정
    ```properties
    jpa.properties.hibernate.generate_statistics=true
    logging.level.org.hibernate.stat=debug
    ```
    - 레거시에서 mybatis와 JPA를 같이 사용하는 경우 각각 다른 방식으로 로깅을 구성해야 하는 불편함
    - 로그 구성이 logging 모듈, DB 모듈로 분리되는 문제
  - 방법 3. AOP 구현 
    ```kotlin
    @Around("execution(* com.hanghae.commerce.data.domain..*(..))")
    @Throws(Throwable::class)
    fun handle(joinPoint: ProceedingJoinPoint): Any {
        val start = Instant.now()
        val result = joinPoint.proceed()
        val executionTime = Instant.now().toEpochMilli() - start.toEpochMilli()
        when {
            executionTime > SLOW_QUERY_THRESHOLD -> logger.error { "${joinPoint.signature} executed in ${executionTime}ms" }
            executionTime > 3000 -> logger.warn { "${joinPoint.signature} executed in ${executionTime}ms" }
            else -> logger.debug { "${joinPoint.signature} executed in ${executionTime}ms" }
        }
        return result
    }
    ```
  - 방법 4. 인프라 설정
    - MySQL을 설치하여 사용하고 있다면, 설정 파일 'my.cnf' 에서 슬로우 쿼리 통계 설정
      ```text
      [mysqld]
      
      general_log=on
      log_output='TABLE'
      slow_query_log = 1
      slow_query_log_file = /var/log/mysql/mariadb-slow.log ->  로그 위치
      long_query_time = 5  - 쿼리 5초 이상인 쿼리
      log_slow_rate_limit = 1
      log_slow_verbosity = query_plan
      log_slow_admin_statements
      ```
      ```sql
      select * from mysql.slow_log;
      ```
    - AWS RDS 를 사용한다면 콘솔에서 슬로우 쿼리 및 알림 정의  
- INFO 수준 로그
  - 사용자 행동 추적 - 상태 변경과 같은 정보성 메시지
  - 시스템 상태 변화 
- 에러 로그를 효율적으로 받아볼 수 있는 방법과 도구들
  - Sentry, ELK, ...
### 설계 (레이어드 아키텍처 with DDD)
- 레이어드 아키텍처의 '표현계층'이라는 워딩에 대한 고민
  - PoEAA에서는 Presentation Layer는 '최종 사용자가 이해하고 사용할 수 있는 형식으로 데이터를 표현하는 역할을 담당'한다고 설명함. 서비스가 제공하는 기능을 시스템이 어떤 형식의 요청과 응답으로 사용자에게 '표현'할 것인 지 추상화한 계층이라고 이해함.
  - PoEAA에서는 말하는 'Presentation'의 의미는 알겠지만, 현업에서 패키지를 통해 아키텍처를 표현할 때 그대로 사용하기에는 다음의 이유로 부적합하다고 생각함.
    - 표현계층이 하는 일은 결국 사용자의 요청을 받을 수 있는 엔드포인트를 정의하고 비즈니스 계층으로 요청을 전달하는 역할을 수행함. 말 그대로 사용자와의 '접점' 역할. 'Presentaion' 이라는 표현이 '사용자에게 정보를 표시하고 사용자가 내린 명령을 수행하는 등의 상호작용을 책임지는 인터페이스'라는 의미를 표현하기에 직관적이지 못하다고 판단됨
    - 'Presentaion'을 사용하면 디렉토리 구조상 맨 아래에 위치함. 사용자와의 접점 역할을 수행하는 레이어가 패키지 구조에서 맨 아래에 위치하는 것에 대한 어색함.
      > application - domain - infrastructure - presentation ?
  - Presentation 대신 '접점'을 표현할 수 있는 'api' 로 네이밍. 
- 표현계층에서 DTO의 책임을 어떻게 정의해야할까
  - 상위계층에서 하위계층으로 의존성의 방향이 흐르도록 하기 위해 비즈니스 계층이 표현계층의 DTO를 의존하게 해선 안됨.  
  - 따라서 DTO를 그대로 도메인 계층으로 전달할 수 없음. 표현계층에서 비즈니스 계층의 데이터 모델로 변환 후 전달하는 과정 필요
- 계층간 모델 변환에 대한 고민
  - 결국 계층 간 모델 변환을 위한 mapper를 어디에 둘 것 인가가 쟁점. 이 때 business 계층이 비즈니스 규칙만 담을 수 있도록 다른 계층에 대한 의존을 줄이는 것이 기준이 됨
  - 표현계층 -> 비즈니스 계층 `Request to Command`
    - 표현계층에 mapper 정의
  - 비즈니스 계층 -> 데이터 접근 계층 `Domain to Entity`
    - 데이터 접근 계층에 mapper 정의
  - 데이터 접근 계층 -> 비즈니스 계층 `Entity to Domain`
    - 데이터 접근 계층에 mapper 정의
  - 비즈니스 계층 -> 표현 계층 `Domain to Response`
    - 표현 계층에 mapper 정의
    - Response 를 생성하기 위해 여러 도메인 모델이 필요한 경우. Response 생성 과정에서 비즈니스 로직이 포함되는 문제가 발생할 가능성이 높음
    - domain model 간의 관계와 규칙을 표현하는 로직을 응집시킬 수 있도록 domain 모델과 response 사이에 별도의 모델 추가 정의가 필요하다고 생각함.
- 비즈니스 계층의 구성
  - 비즈니스 계층의 핵심 책임은 비즈니스 규칙을 수행하는 것.
  - 요구사항이 하나의 도메인 안에서 이루어지는 경우도 있지만, 여러 도메인을 걸쳐 발생하는 경우도 발생할 수 있음. 즉 도메인 간 의존하게 되는 경우 발생. 이 때 도메인 간의 직접적인 결합을 줄이고자, 비즈니스 계층을 application, domain 계층으로 다시 구분 
  - application 은 유즈케이스의 시작점. 여러 domain 서비스들을 조합하고, 실행 순서를 조절하는 역할을 수행.
  - application 의 행위를 논리적으로 하나의 연산으로 봐야한다면 트랜잭션으로 묶을 수 있음. 트랜잭션의 범위가 커지는 것을 경계한다면 각각의 도메인 서비스로 트랜잭션을 묶고 보상 트랜잭션 로직으로 대체하는 것을 고려할 수도 있음
  - domain 계층에서 클래스의 postfix 로 'service' 를 사용하기 때문에, 혼동 및 혼용 방지로 application 계층의 클래스에 정의할 postfix 로 'facade' 를 채택
    > facade 는 '얼굴', '건물의 정면'이라는 의미로 전면에서 다른 클래스들의 복잡한 집합에 대한 단순화된 인터페이스를 제공하는 구조적 디자인 패턴을 의미 
     
    > domain 계층에서 왜 'service' 를 사용하는 지?
    > - 스프링 `@Service` 의 주석에서도 확인할 수 있는데, 'service'라는 네이밍은 DDD에서 차용하였다고 함
  - domain 레이어는 domain model, command, domain service, event 등을 정의. model 들을 활용하여 도메인 로직 구현
- 도메인 주도 설계와 레이어드 아키텍처와의 관계
  - 도메인 주도 설계는 레이어드 아키텍처와는 별개의 개념. 도메인 주도 설계는 도메인 모델을 중심으로 설계하는 방법론이고, 레이어드 아키텍처는 아키텍처의 구조를 레이어로 나누는 방법론일뿐
  - 애플리케이션을 도메인을 중심으로 설계하기 위해 선택 가능한 여러 아키텍처(레이어드, 클린, 헥사고날 등) 중 하나일 뿐. 다만 도메인 주도 설계에 가장 적합한 아키텍처에 대해선 스스로 더 학습의 여지가 있음.
- 도메인 계층의 구성
  - domain bean
    - service, factory, event listener, ...
    - service 에서 validator 를 분리하면, 단순히 책임을 구분하기 위해 클래스를 분리했을 뿐이므로 domain 레이어에 존재함. service 에서 validator 를 의존하는 것은 위상이 깨지는 걸로 봐야 하는건지?
    - service 에서 도메인 모델에 의존하는 건 위상이 깨지는 게 아닌건지? -> 다른 책임을 수행한다고 생각하여 하나의 도메인 서비스가 여러 개의 클래스로 분리되었다면, 위상이 깨지지 않도록 호출 계층을 올려 facade에서 분리된 서비스들을 호출해야 함.
  - domain model
    - 도메인 model, enum, domain event
    - command model 에는 id만? object가?
  - command model
    - 데이터 삽입, 수정 요청 명령에 필요한 데이터 모델
  - 조회는 어떻게?
    - 무슨 네이밍을 사용?
    - 위상을 엄격하게 지켜야 하는 지?
      - 쿼리에서 비즈니스 로직이 없도록 하기위해. 데이터 접근 레이어의 책임을 최소화함. 그렇다면 비즈니스 로직을 담당하는 domain 레이어가 조회 관련 유즈케이스를 수행할 때는 실질적으로 사용이 모호해지는 데 application -> infrastructure 로 가면 안되는 지?
      - 삽입, 수정 명령으로 최대한 비즈니스 로직을 몰아둘 순 있지만, 요구사항의 변화와 발전에 따라 조회 상황에서 비즈니스 로직이 얼마든지 존재할 수 있음. 또한 조회 시 다양한 도메인에 대한 조합이 필요한 경우 도메인간의 관계가 곧 비즈니스 로직이 될 수밖에 없음. 일관된 위상 규칙을 위해서라도 application -> domain -> infrastructure 로의 의존을 지킬 필요가 있음.
- JPA 엔티티와 도메인 모델 분리 
  - JPA 엔티티를 도메인 모델로 활용하게 될 경우 DB에 대한 결합을 충분히 분리했다고 볼 수 있을까? 
    JPA 엔티티가 가지는 1차 캐시, 지연로딩, 영속성 컨텍스트와 같은 특징들은 결국 도메인 레이어가 JPA 라는 데이터 베이스 접근 기술에 의존하게 되는 이유가 됨. 
  - 도메인 모델은 여러 관계자들이 동일한 모습으로 도메인을 이해하고 도메인 지식을 공유할 수 있도록 하는 매개체. 
    도메인 모델을 JPA 라는 기술안에서 고려한다면 상당히 높은 확률로 데이터베이스와의 매핑이라는 맥락안에서 도메인 모델을 정의하게 됨.
    도메인이 제공해야할 기능으로 인한 요소가 아니라 JPA 라는 비기능적인 요소로 인해 도메인 모델에 섞이는 잡음을 최소화하려는 고민 자체가 상당한 리소스가 될 것.
- 도메인 모델과 영속성 모델(JPA 엔티티)을 분리했을 때, 도메인 모델 간에 의존관계 설정을 어떻게 해야할 지 (객체 참조, ID 참조)
  - 비즈니스 계층과 영속성 계층을 분리하여, 비즈니스 계층은 영속성 계층의 구현체를 모르게 했음. 도메일 모델 간 객체 참조를 하게 될 시, JPA Lazy 로딩을 사용하지 못하기 떄문에 영속성 계층에서 미리 완성된 형태로 조립하여
    도메인 계층에 전달해줘야 함.
- 도메인 모델과 영속성 모델(JPA 엔티티)을 분리했을 때, JPA Entity 간에 의존관계 설정을 어떻게 해야할지 (객체 참조, ID 참조)
- 엔티티 간에는 객체 참조인데, 도메인 모델 간에는 ID 만 참조한다면.. ?
  - 도메인 -> 엔티티 변환 시, non null 타입들 때문에 엔티티에 ID 만 셋팅해서 객체를 초기화할 수 있는 방법이 없음
    - 해결책 고민 1. 도메인 모델을 객체 참조로 바꾼다
      - 다른 도메인의 모델을 직접 객체로 참조하게 되는 건 도메인을 정확하게 표현하지 못함. 타 도메인의 모든 프로퍼티에 접근할 수 있게 되는 건, 타 도메인을 어떻게 해석하고 있는 지 충분히 표현하지 못함. 현재 도메인에서 필요한 값들만 도메인 모델로 정의되어야 함
      - 또한 타 도메인의 모델을 직접 참조하는 건 분산시스템 환경에서 관리 리소스를 증가시킬 수 있음. OOP, DDD 관점에서 도메인 간의 결합을 낮추는 방향으로 설계를 하고 있는데 타 도메인의 객체를 직접 참조하는 건 도메인 간 결합을 상당히 높이게 됨.
      - User 와 별개로, TakenExam 도메인 안에서만 유효한 User가 정의되어야 함
      - 하지만 TakenExam 도메인 안에서만 유효한 User를 정의한다고 하더라도 엔티티 모델로 변환 시 채우지 못하는 non null 프로퍼티가 있을 가능성이 여전히 존재함
    - 해결책 고민 2. 엔티티 모델을 ID 참조로 바꾼다.
      - 다른 BC -> 애초의 DDD 설계 목적에 맞게 ID 참조를 선택함으로써 결합도를 낮춤
      - 같은 BC -> 애그리거트 루트 부터 1:1, 1:N 관계의 하위 엔티티들에 대하여 계층형 객체 참조
      - ID 참조일 때 타 BC의 애그리거트는 어디서 조회해야 하는 가?
        - 데이터 접근 계층
          - Querydsl 로 한방 쿼리
            - 쿼리 자체에서 다른 테이블을 직접 의존하기 때문에, 추후 DB 테이블 분리 시 어려움 겪을 수 있음. [결합도 상]
          - 여러 JpaRepository 직접 의존하여 완성된 도메인 모델 조립하여 비즈니스 계층에 전달할 수 있음. [결합도 중]
        - 비즈니스 계층
          - 분산 시스템 환경에서 Client 를 통해 타 시스템의 데이터를 가져오듯이 구성. [결합도 하]
            ```kotlin
            fun findExams(examId: String, userId: String){
                val exams = examReader.read(examId)
                val sources = sourceReader.read(examId)
                val tags = tagReader.read(examId)
                val examQueryModels = aggregate(exams, sources, tags)
            }
            ```     
  - 도메인 모델에서 왜 ID만 참조하는가?
    - 다른 도메인으로 분류했으니까 결합도를 낮추기 위해 ID 만 참조함.
      -> 애초에 다른 도메인이라는 이유로 ID만 참조할 이유가 있을 지에 대해서 고민해보자
- 반대로 도메인 모델 간에는 객체 참조인데, 엔티티 모델 간에는 ID 만 참조한다면..?
  - 도메인 -> 엔티티 변환 시, 도메인 모델에서 ID만 꺼내서 엔티티를 초기화하면 되기 때문에 문제없음
  - 엔티티 -> 도메인 변환 시, JPA 연관관계를 활용한 기능을 활용하지 못하지만 쿼리 수행횟수나 성능상의 손해는 없음
- 도메인 모델과 엔티티 모델을 분리하는 것에 대한 회의감
    - 계층 간 변환 작업이 보일러 플레이트가 상당히 큼
- 비즈니스 계층과 데이터 접근 계층의 모델을 분리할 때, 데이터 접근 계층에서 JPA를 사용했을 때의 효용 고민
  - 유지할 수 있는 JPA 기능
    - 간편한 JPA 의 CRUD 인터페이스
    - Querydsl 을 사용한다면 조회 구문 유지보수성 향상
      - 쿼리 구문을 컴파일 타임에 검증할 수 있음
      - 메서드 추출을 활용한 책임 분리 및 재사용
      - 컴파일 타임 쿼리 검증과 메서드 추출을 활용할 수 있는 다른 라이브러리는 없을까?
        -> [JOOQ](https://www.jooq.org/)
  - 유지하기 어려운 JPA 기능
    - 트랜잭션 내에서 Lazy Loading 을 활용한 DB 데이터 접근.
      - 루트 애그리거트 부터 의존하는 모든 엔티티들은 'spring-data-jpa + Eager loading' 조합으로 한방쿼리 하는게 편리할 수도 있음
      - 그랬을때, Querydsl 로 Paging, Sorting 같은 조회 로직이 필요한 경우에 발생가능한 이슈는...?
    - 1차 캐시
    - 객체 참조를 통한 연관관계 매핑의 편리함
  - 마땅히 대체할만한 데이터 접근 기술은 있는 지? 결국 테이블에 매핑되는 객체는 관리되어야 함. JPA 사용에 따라 주의해야할 점도 많아지지만 테이블과 매핑되는 엔티티를 통한 기본적인 CRUD 인터페이스가 주는 주는 편리함이 꽤 큼.     
- 도메인을 중심으로 설계하기 위해 데이터 접근 영역의 책임을 어느 정도로 제한하는 게 좋을 지
  - 생성, 수정, 삭제는 JPA 에서 복잡한 인터페이스를 제공하지 않음. 데이터 조회 시 다양한 책임들이 발생할 여지가 있음.
  - 비즈니스 로직의 구현을 도메인 계층에서 응집하는 것이 중요한 DDD 관점에서, 데이터 접근 영역의 조건에 이미 여러 로직들이 포함되어 있지는 않은 지 따져볼 필요가 있음.(where, group by, order by, ...)
    - 예를 들어 Spring Data JPA는 메서드 네이밍으로 간편하게 쿼리를 정의할 수 있기 때문에 주로 `findByStatusAndIsDelete()` 같은 메서드들이 많이 정의됨.
    - 위 메서드는 이미 다양한 행위를 포함하고 있음. `findActiveUsers()`와 같이 의미를 내포한 네이밍으로 바꾼다고 하더라도 쿼리에 포함된 조건이 '활성 사용자'를 정의하는 비즈니스 규칙을 가지고 있기에 데이터 접근 영역이 비즈니스 레이어에 대한 책임을 어느정도 가져가게 됨.
    - 데이터 접근 영역은 `findAll()`을 수행. 조회한 전체 사용자 목록을 도메인 계층에서 status, isDelete 값으로 filtering하여 '활성 사용자'를 추출하는 로직이 정의되어야 함.
  - 계층별 책임을 분리함에 있어 어느 정도 성능적인 손해를 감수할 수 있을 지에 대한 판단에 따라 데이터 접근 영역의 책임을 제한할 수 있어야 함.   
- 각각의 레이어를 모듈로 분리하는 것의 장단점
  - presentation, business, persistence 레이어를 각각의 모듈로 분리하면, 각 모듈의 책임이 명확해지고, 모듈 간의 의존성의 방향을 강제할 수 있다는 장점이 있음. 실수로라도 JPA Repository에서 도메인 모델을 의존한다거나, Controller 에서 바로 JPA Repository 를 의존하는 일이 발생할 수 없음.
  - 모듈 분리 시 모듈 간의 의존성을 관리해야 하는 비용 발생. 참조하는 다른 모듈의 클래스를 찾기 위해 프로젝트 트리를 위아래로 크게 스크롤하는 번거로움이 발생함. 프로젝트 초기 복잡도가 크게 증가하지 않은 상황에서 이러한 비용들은 불필요하게 느껴질 수 있음

### 구현
- 트랜잭션의 범위를 최소화 했을 때의 장단점은?
  - 트랜잭션은 Row 단위로 데이터베이스의 리소스를 잠그고, 병렬 처리를 제한함. 범위를 최소화한다면 Lock 시간이 줄어들어 성능 향상 차원에서 유리함.
  - 보통 애플리케이션에서 작성되는 DB 관련 동작들을 논리적으로 하나의 연산으로 봐야한다면 트랜잭션으로 묶게 됨. DB 관련 동작이 아닌 부분으로 인해 트랜잭션이 길어지지는 않는 지 점검할 필요가 있음.
    ```java
    @Transactional
    public void appendReview(ReviewAppendCommand command){
        verifyReview(command);
        saveReview(command); <- 트랜잭션 관련 로직은 이 라인뿐 
        notification(command);  
    }
    ```
  - DB 락의 범위와 세션 유지시간이 줄어들면 서로 다른 트랜잭션 간의 자원 경쟁이 발생하지 않도록 시간적 여유가 늘어나기 때문에 데드락 발생 가능성이 줄어듬.
  - 비즈니스 로직이 여러 작업을 포함하는 경우, 하나의 트랜잭션으로 그 작업들을 묶는 것이 더 편리함. 트랜잭션을 나누면 실패 케이스에 대한 로직을 별도 구현해야 하므로 로직 관리가 복잡해지게 됨. 트랜잭션 관리 리소스가 늘어남에 따라 데이터 정합과 일관성에 대한 책임이 개발자에게 더 큰 부담이 될 수 있음.
- Circuit Breaker 는 어떤 용도로 사용하는 지?
  - 외부 시스템에 의존하는 경우, 외부 시스템의 장애로 인해 애플리케이션의 전체적인 성능이 저하되는 것을 방지하기 위해 사용.
  - 외부 시스템에서 장애가 발생한 경우, 즉 정상 응답이 돌아오지 못하고 지연되고 있는 상황에서 계속하여 요청을 쌓게될 경우 정상화만 늦춰지게 됨. 
    애플리케이션 전체로 장애가 전파되지 않도록 외부 시스템의 장애를 감지하고, 장애가 발생한 경우에는 외부 시스템에 대한 요청을 차단함으로써 애플리케이션의 전체적인 성능 저하를 방지함.
  - 외부 시스템이란 써드파티 API 일수도 있고, MSA 라면 다른 마이크로서비스를 의미할 수도 있음.
  - 최근 요청들을 감시하여 실패율을 평가하고, 지정된 threshold에 따라 서킷을 오픈함. 서킷 오픈 시 요청을 차단하고 특정 시간 or 특정 요청 동안은 fallback 처리. 
    서킷 오픈 시간이 지나면 서킷을 half-open 상태로 전환하고, 요청을 일부 허용함. 허용된 일부 요청이 성공하면 서킷을 닫고, 실패하면 다시 오픈하는 프로세스
  - fallback으로 또 다른 써드파티 API 를 연동하여 호출할 수도 있고, 캐싱된 데이터를 응답할 수도 있음. 
- RateLimiter 는 어떤 용도로 사용하는 지?
- Retry 의 expotional backoff, jitter 란 무엇인지?
