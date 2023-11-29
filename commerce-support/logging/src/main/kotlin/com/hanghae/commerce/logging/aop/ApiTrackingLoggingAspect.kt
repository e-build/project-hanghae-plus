package com.hanghae.commerce.logging.aop

import org.aspectj.lang.annotation.*
import org.slf4j.MDC
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*

@Aspect
@Order(0)
@Component
class ApiTrackingLoggingAspect {
//        "@annotation(org.springframework.web.bind.annotation.GetMapping) " +
//            "|| @annotation(org.springframework.web.bind.annotation.PostMapping) " +
//            "|| @annotation(org.springframework.web.bind.annotation.RequestMapping)",
    @Pointcut(
        "within(@org.springframework.web.bind.annotation.RestController *) " +
            "|| within(@org.springframework.stereotype.Controller *)" +
            "execution(* *(..)) &&",
    )
    fun apiHandlerMethods() {
    }

    @Before(value = "apiHandlerMethods()")
    fun handle() {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        MDC.put("traceId", request.getHeader("X-Request-Id") ?: UUID.randomUUID().toString())
        MDC.put("sessionId", request.session.id)
        MDC.put("userId", UUID.randomUUID().toString())
        println("execute")
    }

    @AfterReturning("apiHandlerMethods()")
    fun afterReturningTransactionalMethod() {
        clearMDC()
    }

    @AfterThrowing("apiHandlerMethods()")
    fun afterThrowingTransactionalMethod() {
        clearMDC()
    }

    private fun clearMDC() {
        MDC.clear()
    }
}
