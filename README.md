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

# 프로젝트 이후 비판적인 고민
### Github Action
- PR 시, ktlint의 피드백이 라인에 노출됨으로 인해 코드 가독성을 해쳐 코드를 보는 것이 어려워짐. 좀 더 깔끔한 방법은 없을 지
  - TBU
- CI 에서 unitTest와, integrationTest 를 연달아 실행하고 있음. 분리된 unitTest 와 integrationTest 를 활용해서 CI Phase 를 좀 더 효율적으로 구성하는 방법은 없을지
  - TBU
- 운영배포 시 흔히 git tag를 활용하는 데, 수동으로 입력할 때 태그명을 잘못 입력하게 된다면? 휴먼 에러를 줄일 수 있는 배포 구성은?
  - TBU
### Testing
- 단위테스트의 단위란
  - TBU
  - 해당 객체가 수행해야하는 책임의 범위
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
- 레이어드 아키텍처의 핵심은 책임과 위상
- 레이어드 아키텍처의 '표현계층'이라는 워딩에 대한 고민
  - 결국 사용자의 요청을 받을 수 있는 엔드포인트를 정의하고 도메인 영역으로 요청을 전달하는 역할을 수행함. 말 그대로 사용자와의 접점
  - presentaion 이라는 표현이 '사용자에게 정보를 표시하고 사용자가 내린 명령을 수행하는 등의 상호작용을 책임지는 인터페이스'라는 의미를 직관적으로 나타내는 단어로써 부적합하다고 생각.
  - 그렇다면 왜 '표현계층' 이라는 단어가 대중적으로 사용될까? 애플리케이션이 어떤 기능을 서비스하는 가의 관점에서 보면, 서비스가 제공하는 기능을 시스템이 어떤 형식의 요청과 응답으로 사용자에게 '표현'하고 있는 지로 이해할 수 있음.     
  - 또한 presentaion을 사용하면 디렉토리 구조상 맨 아래에 위치함. 사용자와의 접점 역할을 수행하는 레이어가 패키지 구조에서 맨 아래에 위치하는 것에 대한 어색함.
    > application - domain - infrastructure - presentation
  - presentation 이라는 네이밍 대신 '접점'을 표현할 수 있는 'api' 로 정의. 
- 표현계층에서 DTO의 책임을 어떻게 제한하는 것이 도메인 계층과의 결합도를 낮출 수 있을 지
  - TBU
  - 도메인 계층이 표현계층에 의존해서는 안됨. 따라서 DTO를 그대로 도메인 계층으로 전달할 수 없음. 표현 계층과 도메인 계층의 결합도를 낮추려면 도메인 계층의 데이터 모델로 변환화는 과정 필요 
  - DTO - requset model, response model 
    - request to command 
      - application 계층을 건너띄고 domain 계층의 command 에 접근하면 위상이 깨지지 않나? command 를 애플리케이션 계층에 둔다면?
    - domain to response 
      - Response 를 생성하기 위해 여러 도메인 모델이 필요한 경우. Response 생성 과정에서 비즈니스 로직이 포함되는 문제가 발생할 가능성이 높음
      - domain model 간의 관계와 규칙을 표현하는 로직을 응집시킬 수 있도록 domain 모델과 response 사이에 별도의 모델 추가 정의가 필요하다고 생각함.
- 계층간 모델 변환에 대한 고민
  - TBU
- 도메인 계층
  - 도메인 계층의 핵심 책임은 비즈니스 규칙을 수행하는 것.
- Application 은 왜 필요한 지
- Application 계층의 클래스 네이밍
  - facade?, service?
- domain
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
