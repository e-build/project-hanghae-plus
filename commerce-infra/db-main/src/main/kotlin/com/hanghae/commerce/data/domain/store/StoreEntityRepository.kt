package com.hanghae.commerce.data.domain.store

import com.hanghae.commerce.store.domain.Store
import com.hanghae.commerce.store.infrastructure.StoreRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class StoreEntityRepository(
    private val jpaStoreRepository: JpaStoreRepository,
) : StoreRepository {
    override fun save(store: Store): Store {
        return jpaStoreRepository.save(store.toEntity()).toDomain()
    }

    override fun search(name: String): List<Store> {
        return jpaStoreRepository.findByName(name)
            .map { storeEntity -> storeEntity.toDomain() }
    }

    override fun deleteAll() {
        jpaStoreRepository.deleteAllInBatch()
    }

    override fun findById(id: String): Store? {
        return jpaStoreRepository.findByIdOrNull(id)?.toDomain()
    }

    override fun findAll(): List<Store> {
        return jpaStoreRepository.findAll()
            .map { storeEntity -> storeEntity.toDomain() }
    }
}
