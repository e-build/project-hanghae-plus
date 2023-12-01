package com.hanghae.commerce.logging.aop

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.hibernate.resource.transaction.spi.TransactionStatus
import org.slf4j.MDC
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.TransactionDefinition
import java.util.*

@Aspect
@Component
@Profile("disabled")
class TransactionLoggingAspect(
    @PersistenceContext
    private val entityManager: EntityManager,
) {
    //    @Around("execution(* com.hanghae.commerce.data.domain.*(..))")
    @Around("execution(* org.springframework.transaction.support.AbstractPlatformTransactionManager.*(..))")
    fun handle(pjp: ProceedingJoinPoint): Any {
        val methodName = pjp.signature.name
        val definition = resolveTransactionDefinition(pjp)

        if (methodName == "getTransaction") {
            logTransactionStart(definition)
        }

        val result: Any = pjp.proceed()

        if (result is TransactionStatus) {
            if ("commit" == methodName || "rollback" == methodName) {
                logTransactionEnd(result)
            }
        }

        // TODO: AOP 로 감싸면 AbstractPlatformTransactionManager.Logger 에서 NPE 발생
        return result
    }

    private fun resolveTransactionDefinition(pjp: ProceedingJoinPoint): TransactionDefinition? {
        if (pjp.args.isNotEmpty() && pjp.args[0] is TransactionDefinition) {
            return pjp.args[0] as TransactionDefinition
        }
        return null
    }

    private fun logTransactionStart(definition: TransactionDefinition?) {
        val parentTransactionId: String? = TransactionContextHolder.getCurrentTransactionId()
        val currentTransactionId = UUID.randomUUID().toString()
        TransactionContextHolder.startTransaction(currentTransactionId)
        MDC.put("txId", currentTransactionId)
        MDC.put("ptxId", parentTransactionId ?: "None")
        MDC.put("tx-propagation", propagationBehaviorToString(definition?.propagationBehavior ?: 0))
        MDC.put("tx-isolation", isolationLevelToString(definition?.isolationLevel ?: TransactionDefinition.ISOLATION_DEFAULT))
        MDC.put("tx-status", "Started")
    }

    private fun logTransactionEnd(status: TransactionStatus) {
        TransactionContextHolder.endTransaction()
        MDC.put("transactionStatus", status.name)
        MDC.clear()
    }

    // 전파 수준을 문자열로 변환
    private fun propagationBehaviorToString(propagationBehavior: Int): String {
        return when (propagationBehavior) {
            TransactionDefinition.PROPAGATION_REQUIRED -> "REQUIRED"
            else -> "OTHER"
        }
    }

    // 격리 수준을 문자열로 변환
    private fun isolationLevelToString(isolationLevel: Int): String {
        return when (isolationLevel) {
            TransactionDefinition.ISOLATION_DEFAULT -> "DEFAULT"
            else -> "OTHER"
        }
    }
}
