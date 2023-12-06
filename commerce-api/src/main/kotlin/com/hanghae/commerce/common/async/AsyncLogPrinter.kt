package com.hanghae.commerce.common.async

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AsyncLogPrinter {
    val logger = KotlinLogging.logger { }

    @Async("fooTaskExecutor")
    fun print() {
        for (i in 1..10) {
            Thread.sleep(100)
            logger.info { "foo foo $i" }
        }
    }
}
