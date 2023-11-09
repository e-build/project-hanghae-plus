package com.hanghae.commerce.store.infrastructure

import com.hanghae.commerce.store.domain.Store
import org.springframework.stereotype.Component

@Component
class StoreReader(
    private val storeRepository: StoreRepository,
) {

    fun findById(id: String): Store {
        return storeRepository.findById(id) ?: throw IllegalArgumentException()
    }

    fun findAll(): List<Store> {
        return storeRepository.findAll()
    }

    fun search(name: String): List<Store> {
        return storeRepository.search(name = name)
    }
}
