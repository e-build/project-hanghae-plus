package com.hanghae.commerce.data.domain.store

import org.springframework.data.jpa.repository.JpaRepository

interface JpaStoreRepository : JpaRepository<StoreEntity, String> {
    fun findByUserId(userId: String): List<StoreEntity>
    fun findByName(name: String): List<StoreEntity>
}
