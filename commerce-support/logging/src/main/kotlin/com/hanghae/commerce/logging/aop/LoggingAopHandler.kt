package com.hanghae.commerce.logging.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.DefaultTransactionStatus
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.util.*

@Aspect
@Component
class LoggingAopHandler {
    @Autowired
    private lateinit var transactionManager: PlatformTransactionManager

    @Around(value = "@annotation(org.springframework.web.bind.annotation.GetMapping)")
    @Throws(Throwable::class)
    fun retry(pjp: ProceedingJoinPoint): Any? {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request

        MDC.put("traceId", request.getHeader("X-Request-Id") ?: UUID.randomUUID().toString())
        MDC.put("sessionId", request.session.id)
        MDC.put("userId", UUID.randomUUID().toString())

        val transactionStatus = transactionManager.getTransaction(TransactionDefinition.withDefaults())
        val transactionId = (transactionStatus as DefaultTransactionStatus).transaction.toString()
        MDC.put("txId", transactionId)
        val proceed = pjp.proceed()
        MDC.clear()
        println("execute")
        return proceed
    }
}
