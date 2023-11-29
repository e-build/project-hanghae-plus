package com.hanghae.commerce.logging.aop

import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import java.time.Instant

@Aspect
@Component
class QueryPerformanceLoggingAspect {
    val logger = KotlinLogging.logger { }
    private val SLOW_QUERY_THRESHOLD: Long = 10 // 500ms 이상을 슬로우 쿼리로 가정

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
}
