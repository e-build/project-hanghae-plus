package com.hanghae.commerce.store.domain

import com.hanghae.commerce.common.async.AsyncLogPrinter
import com.hanghae.commerce.store.infrastructure.StoreReader
import org.springframework.stereotype.Service

@Service
class StoreReadService(
    private val storeReader: StoreReader,
    private val asyncLogPrinter: AsyncLogPrinter,
) {
    fun read(): List<Store> {
        asyncLogPrinter.print()
        return storeReader.findAll()
    }

    fun read(storeId: String): Store {
        return storeReader.findById(storeId)
    }
}
