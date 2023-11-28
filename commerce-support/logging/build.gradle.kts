dependencies {
    api("io.github.microutils:kotlin-logging-jvm:2.0.10")
    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")
    implementation(group = "ca.pjer", name = "logback-awslogs-appender", version = "1.6.0") // 로그백 의존성
    implementation("org.codehaus.janino:janino:3.1.7") // 프로퍼티 제어 in xml
}
