package com.hanghae.commerce.logging.aop

import mu.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.time.Instant

@Aspect
@Order(1)
@Component
class ApiPerformanceLoggingAspect {

    val logger = KotlinLogging.logger { }

    @Pointcut(
        "within(@org.springframework.web.bind.annotation.RestController *) " + "|| within(@org.springframework.stereotype.Controller *)" + "execution(* *(..)) &&",
    )
    fun apiHandlerMethods() {
    }

    @Around("apiHandlerMethods()")
    fun handle(pjp: ProceedingJoinPoint): Any {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        logger.info { "${request.log()} : start" }
        val startTime = Instant.now()
        val result = pjp.proceed()
        request.setAttribute("startTime", Instant.now())
        val endTime = Instant.now()
        val processingTimeMillis = endTime.toEpochMilli() - startTime.toEpochMilli()

        when {
            processingTimeMillis > 5000 -> logger.error { "${request.log()} Significant Slow API: ${processingTimeMillis}ms" }
            processingTimeMillis > 3000 -> logger.warn { "${request.log()} Slow API: ${processingTimeMillis}ms" }
            processingTimeMillis < 200 -> logger.info { "${request.log()} : end" }
        }
        return result
    }
}
