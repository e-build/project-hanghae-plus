package com.hanghae.commerce.store.infrastructure

import com.hanghae.commerce.store.domain.Store

interface StoreRepository {
    fun save(store: Store): Store
    fun deleteAll()
    fun findById(id: String): Store?
    fun findAll(): List<Store>
    fun search(name: String): List<Store>
}
