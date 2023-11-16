# 개요
- 이커머스 도메인 전반에 대한 백엔드 설계
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
- Api - 사용자에게 정보를 표시하고 사용자가 내린 명령을 수행하는 등의 상호작용을 책임지는 인터페이스
- Application - 표현 영역과 도메인 영역을 연결하는 창구 역할. 주로 도메인 개체 간 흐름 제어만을 수행하는 단순한 형태
- Domain - 비즈니스 정책 및 규칙을 담당하는 책임 수행
- Infrastructure - DB, Redis, 외부 API 와 같은 애플리케이션이 의존하는 외부 시스템에 대한 인터페이스를 제공하는 책임 수행

# 프로젝트 이후 추가 학습
### Github Action
- PR 시, ktlint의 피드백이 라인에 노출됨으로 인해 코드 가독성을 해쳐 코드를 보는 것이 어려워짐.
  - ScaCap/action-ktlint 의 불편함 
    - reporter 속성으로 `github-pr-review`, `github-pr-check` 를 지정할 수 있는데 둘 다 결국 PR에서 변경된 Line 별로 ktlint 의 피드백이 생성됨
    - 리뷰 시 라인에 lint 피드백이 달려서 코드 가독성을 심각하게 낮춘다고 판단함.
    - .editorconfig 설정대로 github action 에서의 lint 체크 수행을 위해선 별도 설정 필요
  - gradle task 로 직접 작성
    - `./gradlew ktlintCheck`
      - ktlint 피드백은 github action의 job detail을 통해 확인. 페이지를 한단계 들어가야 한다는 불편함이 있음
      - ktlint 검사로 인해 PR Check가 실패하는 경우, 사실 한개라도 발생하면 로컬에서 다시 수정하여 PR 해보게 된다는 점에서 ktlint 검증 실패가 코드의 어떤 라인에서 발생했는 지 보다, 발생했는지 안했는 지 여부가 더 중요하다고 판단함.  
    > 로컬에서 commit 이전에 ktlintCheck 자동수행으로 확인해볼 수 있도록 `./gradlew addKtlintCheckGitPreCommitHook` 활용 가능
- CI 에서 unitTest와, integrationTest 를 분리하여 구성하였을 때 얻을 수 있는 점은 무엇인지 
  - Mike Cohn이 처음으로 정의한 테스트 피라미드의 하단에는 유닛 테스트, 중간에는 서비스 테스트, 상단에는 UI 테스트가 표시됨. 이름은 다소 부정확할 수 있지만 그 전제는 합리적으로 보임.
    > 1. <i>**빠르고 쉽게**</i> 실행 가능한 자동화된 유닛 테스트로 강력한 기반을 다지고, 
    > 2. 작성하기 더욱 복잡하며 실행 시간도 더 오래 소요되는 테스트로 나아간 후, -> 통합 테스트
    > 3. 마지막에 소수의 가장 복잡한 테스트로 완료하는 것입니다. -> E2E 테스트, 성능 테스트, UI 테스트
   - 타 컴포넌트와 의존하지 않음으로써 단위테스트가 가지는 큰 장점인 빠른 피드백을 활용할 수 있음
   - 통합테스트 실행 시 필요한 외부 컴포넌트들을 사전 구성하는 작업은 CI 과정에서 꽤 큰 시간을 소요할 수 있음. ex) 테스트 DB, 테스트 Redis, 테스트 Seed data 등  
   - 속도 측면에서의 장점과 별개로 애플리케이션 검증 차원에서 단위 테스트가 뼈대 역할을 한다면, 통합 테스트는 각각의 뼈가 잘 연결되어있는 지 확인하는 역할이라고 봄. 책임이 엄격히 분리됨으로써 이후 작성될 수 있는 테스트들의 목적이 명확해지고, 이는 협업 상황에서 테스트를 다루는 방향성을 제시함
   - 결론 - CI 환경에서 단위 테스트의 속도 활용 가능, 개발 초기 단계에서 단위 테스트와 통합 테스트를 별도 구성하여 각각 관리해야 하는 불편함
- 운영배포 시 흔히 git tag를 활용하는 데, 수동으로 입력할 때 태그명을 잘못 입력하게 된다면? 휴먼 에러를 줄일 수 있는 배포 구성은?
  - TBU
### Testing
- 단위테스트의 단위란
  - TBU
  - 테스트 대상 객체가 수행해야하는 책임의 범위
  - 참고 - [The Practical Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html#WhatsAUnit)
- 단위, 통합, E2E 테스트는 각각 어느 상황에 작성해야 할 지
  - TBU
- 테스트 간 동일한 코드에 대한 검증이 중복되는 것을 최소화 하도록 고려해야할 지
  - TBU
- 외부 시스템을 연동할 때 테스트 코드를 어떻게 활용하는 게 좋을 지
  - TBU
- 아키텍처를 테스트하고 싶을 때 사용할 수 있는 도구도 있을 지
  - ArchUnit
- TDD 과정 중 테스트 코드와 어느 정도 완성된 실제 코드가 함께 있는 경우가 많다. 해당 상황에서 테스트 코드와 프로덕션 코드의 커밋 순서나 커밋 메세지 작성 방식은 어떻게 하는 것이 좋을 지
  - TBU
- 테스트 상황 설정을 위해 더미 데이터 모델 객체를 정의할 때, 일일이 데이터를 세팅하는 것 말고 좀 더 편리한 방법은 없을 지
  - Instancio, Fixture Monkey, Easy Random
### Logging & Monitoring
- INFO 레벨에는 어떤 내용들을 찍어야 할까?
  - TBU
- 에러 로그를 효율적으로 받아볼 수 있는 방법과 도구들은 무엇이 있을까?
  - TBU
### 설계
- 레이어드 아키텍처의 '표현계층'이라는 워딩에 대한 고민
  - PoEAA에서는 Presentation Layer는 '최종 사용자가 이해하고 사용할 수 있는 형식으로 데이터를 표현하는 역할을 담당'한다고 설명함. 서비스가 제공하는 기능을 시스템이 어떤 형식의 요청과 응답으로 사용자에게 '표현' 할것인 지 추상화한 계층이라고 이해함.
  - PoEAA에서는 말하는 'Presentation'의 의미는 알겠지만, 현업에서 아키텍처 구성 시 그대로 사용하기에는 다음 같이 이유로 부적합하다고 생각함.
    - 표현계층이 하는 일은 결국 사용자의 요청을 받을 수 있는 엔드포인트를 정의하고 비즈니스 계층으로 요청을 전달하는 역할을 수행함. 말 그대로 사용자와의 '접점' 역할. 'Presentaion' 이라는 표현이 '사용자에게 정보를 표시하고 사용자가 내린 명령을 수행하는 등의 상호작용을 책임지는 인터페이스'라는 의미를 표현하기에 직관적이지 못하다고 판단됨
    - 'Presentaion'을 사용하면 디렉토리 구조상 맨 아래에 위치함. 사용자와의 접점 역할을 수행하는 레이어가 패키지 구조에서 맨 아래에 위치하는 것에 대한 어색함.
      > application - domain - infrastructure - presentation ?
  - Presentation 대신 '접점'을 표현할 수 있는 'api' 로 네이밍 결정. 
- 표현계층에서 DTO의 책임을 어떻게 정의해야할까
  - 상위계층에서 하위계층으로 의존성의 방향이 흐르도록 하기 위해 비즈니스 계층이 표현계층의 DTO를 의존하게 해선 안됨.  
  - 따라서 DTO를 그대로 도메인 계층으로 전달할 수 없음. 표현계층에서 비즈니스 계층의 데이터 모델로 변환 후 전달하는 과정 필요
- 계층간 모델 변환에 대한 고민
  - 표현계층 -> 비즈니스 계층 `Request to Command`
    - TBU
  - 비즈니스 계층 -> 데이터 접근 계층 `Domain to Entity`
    - TBU
  - 데이터 접근 계층 -> 비즈니스 계층 `Entity to Domain`
    - TBU
  - 비즈니스 계층 -> 표현 계층 `Domain to Response`
    - Response 를 생성하기 위해 여러 도메인 모델이 필요한 경우. Response 생성 과정에서 비즈니스 로직이 포함되는 문제가 발생할 가능성이 높음
    - domain model 간의 관계와 규칙을 표현하는 로직을 응집시킬 수 있도록 domain 모델과 response 사이에 별도의 모델 추가 정의가 필요하다고 생각함.
- 비즈니스 계층
  - 비즈니스 계층의 핵심 책임은 비즈니스 규칙을 수행하는 것.
- Application 은 왜 필요한 지
- Application 계층의 클래스 네이밍
  - facade?, service?
- Domain
  - domain bean
    - service, factory, event listener
    - service 에서 validator 를 분리하면, 단순히 책임을 구분하기 위해 클래스를 분리했을 뿐이므로 domain 레이어에 존재함. service 에서 validator 를 의존하는 것은 위상이 깨지는 걸로 봐야 하는건지?
    - service 에서 도메인 모델에 의존하는 건 위상이 깨지는 게 아닌건지?
  - domain object
    - domain model, enum, domain event,
    - command model 에는 id만? object가?
  - command object
    - 데이터 삽입, 수정 요청에 대한 명령에 필요한 데이터 모델
  - 조회는 어떻게?
    - 무슨 네이밍을 사용?
    - 위상을 엄격하게 지켜야 하는 지?
      - 쿼리에서 비즈니스 로직이 없도록 하기위해. 영속성 레이어의 책임을 최소화했다. 그렇다면 비즈니스 로직을 담당하는 domain 레이어가 조회 관련 유즈케이스를 수행할 때는 실질적으로 사용이 모호해지는 데 application -> infrastructure 로 가면 안되는 지?
- JPA 엔티티를 그대로 도메인 모델로 사용해도 될까?
  - JPA 엔티티를 도메인 레이어에서 활용하게 될 경우 DB에 대한 결합을 충분히 분리했다고 볼 수 있을까?
  - TBU
- JPA 엔티티를 도메인 모델로 사용하지 않는 상황에서, 도메인 모델간에 의존관계 설정을 어떻게 해야할 지 (객체 참조, ID 참조)
  - TBU
- JPA 엔티티를 도메인 모델로 사용하지 않는 상황에서, JPA Entity 간에 의존관계 설정을 어떻게 해야할지 (객체 참조, ID 참조)
  - TBU
- 도메인을 중심으로 설계하기 위해 데이터 접근 영역의 책임을 어느 정도로 제한하는 게 좋을 지    
  - TBU
- DDD와 Layered Architecture 와의 관계는 무엇인지
  - TBU
- 각각의 레이어를 모듈로 분리하는 것의 장단점
  - TBU
- 외래키를 최소화하면서 테이블 간의 연관관계 제어할 수 있는 방법은?
  - TBU
- DB 설계 시 외래키를 사용하지 않으면 어떤 장단점들이 있을까?
  - TBU

### 구현
- 동시성 이슈를 해결하기 위한 방법들로는 어떤 것들이 있을까?
  - 낙관락, 비관락, 분산락, ...
  - TBU
- 현업에서 트랜잭션을 불필요하게 많이 사용하고 있지는 않나?
  - 트랜잭션이란 하나의 연산으로 다뤄지는 하는 로직의 논리적인 단위.
  - TBU
- 트랜잭션의 범위를 최소화 했을 때의 장단점은?
  - 장애 전파를 최소화 할수 있음. DB 락의 범위와 세션 유지시간을 줄일 수 있음.
  - 트랜잭션의 범위를 최소화한다면 어느 기준을 가지고 트랜잭션을 묶어야 하는 지?
  - TBU
- 장애허용 기술들
  - Circuit Breaker, RateLimiter, Retry 는 어떤 용도로 사용되는 지?
    - TBU
  - 장애허용 기술들을 서로 조합하여 사용할 수도 있는 지?
    - ex) Circuit Breaker + Retry
    - TBU
  - Retry 의 expotional backoff, jitter 란 무엇인지?
    - TBU
- 성능테스트
  - TBU
