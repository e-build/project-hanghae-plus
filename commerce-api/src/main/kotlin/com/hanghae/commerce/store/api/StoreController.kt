package com.hanghae.commerce.store.api

import com.hanghae.commerce.store.api.dto.*
import com.hanghae.commerce.store.application.StoreFacade
import org.springframework.web.bind.annotation.*

@RestController
class StoreController(
    private val storeFacade: StoreFacade,
) {
    @PostMapping("/store")
    fun createStore(@RequestBody request: CreateStoreRequest): CreateStoreResponse {
        return CreateStoreResponse.of(
            storeFacade.resisterStore(request.toCommand()),
        )
    }

    @GetMapping("/store")
    fun getStore(@RequestBody request: GetStoreRequest): GetStoreResponse {
        return GetStoreResponse.of(
            storeFacade.getStore(request.storeId),
        )
    }

    @GetMapping("/stores")
    fun getStores(): GetStoreListResponse {
        return GetStoreListResponse.of(
            storeFacade.getStores(),
        )
    }
}
