package com.hanghae.commerce.store.domain

import com.hanghae.commerce.store.infrastructure.StoreReader
import org.springframework.stereotype.Service

@Service
class StoreReadService(
    private val storeReader: StoreReader,
) {
    fun read(): List<Store> {
        return storeReader.findAll()
    }

    fun read(storeId: String): Store {
        return storeReader.findById(storeId)
    }
}
