package com.hanghae.commerce.store.api

import com.hanghae.commerce.store.api.dto.*
import com.hanghae.commerce.store.application.StoreFacade
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/stores")
class StoreController(
    private val storeFacade: StoreFacade,
) {
    @PostMapping
    fun createStore(@RequestBody request: CreateStoreRequest): CreateStoreResponse {
        return CreateStoreResponse.of(
            storeFacade.resisterStore(request.toCommand()),
        )
    }

//    @Operation(
//        summary = "Store single item inquiry",
//        description = """
//Search for a specific store.
//- [jira ticket](...link_url...)
//- If you pass storeId to the URL path, store information corresponding to the ID will be returned.
//
//#### Custom exception case
//| Http Status | Error Code  | Error Message | Error Data | Remark   |
//|-------------|-------------|--------------|------------|-----------|
//| 403         | NO_PERMISSION       |This request is only available to administrators.  |            |           |
//| 400         | STORE_NOT_FOUND       |Store not found.   |            |           |
//| 400         | STORE_WAS_DELETED       |This store has been deleted.  |            |           |
//"""
//    )
    @GetMapping("/{storeId}")
    fun getStore(
        @PathVariable storeId: String,
    ): GetStoreResponse {
        return GetStoreResponse.of(
            storeFacade.getStore(storeId),
        )
    }

    @GetMapping
    fun getStores(): GetStoreListResponse {
        return GetStoreListResponse.of(
            storeFacade.getStores(),
        )
    }
}
