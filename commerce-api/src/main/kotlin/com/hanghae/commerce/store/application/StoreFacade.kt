package com.hanghae.commerce.store.application

import com.hanghae.commerce.store.domain.Store
import com.hanghae.commerce.store.domain.StoreReadService
import com.hanghae.commerce.store.domain.StoreResisterService
import com.hanghae.commerce.store.domain.command.StoreCommand
import org.springframework.stereotype.Component

@Component
class StoreFacade(
    private val storeResisterService: StoreResisterService,
    private val storeReadService: StoreReadService,
) {

    fun resisterStore(command: StoreCommand.Resister): Store {
        return storeResisterService.resister(command)
    }

    fun getStores(): List<Store> {
        return storeReadService.read()
    }

    fun getStore(storeId: String): Store {
        return storeReadService.read(storeId)
    }
}
