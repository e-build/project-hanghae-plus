package com.hanghae.commerce.store.domain

import com.hanghae.commerce.store.infrastructure.StoreWriter
import com.hanghae.commerce.store.domain.command.StoreCommand
import com.hanghae.commerce.user.domain.UserReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class StoreResisterService(
    private val storeWriter: StoreWriter,
    private val userReader: UserReader,
    private val storeResisterValidator: StoreResisterValidator,
) {

    @Transactional
    fun resister(command: StoreCommand.Resister): Store {
        val user = userReader.findById(command.userId)
        val store = Store(
            name = command.name,
            userId = user.id,
        )
        storeResisterValidator.validate(user, store)
        return storeWriter.save(store)
    }
}
