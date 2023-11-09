package com.hanghae.commerce.store.infrastructure

import com.hanghae.commerce.store.domain.Store
import org.springframework.stereotype.Component

@Component
class StoreWriter(
    private val storeRepository: StoreRepository,
) {
    fun save(store: Store): Store {
        return storeRepository.save(store)
    }

    fun deleteAll() {
        return storeRepository.deleteAll()
    }
}
