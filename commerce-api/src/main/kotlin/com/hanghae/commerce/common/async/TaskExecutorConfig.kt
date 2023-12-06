package com.hanghae.commerce.common.async

import com.hanghae.commerce.logging.MDCCopyTaskDecorator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@EnableAsync
@Configuration
class TaskExecutorConfig {

    @Bean(name = ["fooTaskExecutor"])
    fun fooTaskExecutor(): Executor {
        val taskExecutor = ThreadPoolTaskExecutor()
        taskExecutor.corePoolSize = 1
        taskExecutor.maxPoolSize = 5
        taskExecutor.queueCapacity = 10
        taskExecutor.setAllowCoreThreadTimeOut(true)
        taskExecutor.keepAliveSeconds = 30
        taskExecutor.setThreadNamePrefix("foo-task-")
        taskExecutor.setTaskDecorator(MDCCopyTaskDecorator())
        taskExecutor.initialize()
        return taskExecutor
    }
}
