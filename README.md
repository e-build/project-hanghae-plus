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
- Presentation - 사용자에게 정보를 표시하고 사용자가 내린 명령을 수행하는 등의 상호작용을 책임지는 인터페이스
- Application - 표현 영역과 도메인 영역을 연결하는 창구 역할. 주로 도메인 개체 간 흐름 제어만을 수행하는 단순한 형태
- Domain - 비즈니스 정책 및 규칙을 담당하는 책임 수행
- Infrastructure - DB, Redis, 외부 API 와 같은 애플리케이션이 의존하는 외부 시스템에 대한 인터페이스를 제공하는 책임 수행

# 추가 고민 정리
