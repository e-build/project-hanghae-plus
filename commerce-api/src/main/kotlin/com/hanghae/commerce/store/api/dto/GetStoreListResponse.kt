package com.hanghae.commerce.store.api.dto

import com.hanghae.commerce.store.domain.Store

data class GetStoreListResponse(
    val list: List<GetStoreResponse>,

) {
    companion object {
        fun of(stores: List<Store>): GetStoreListResponse {
            return GetStoreListResponse(
                stores.map { store ->
                    GetStoreResponse(
                        id = store.id,
                        name = store.name,
                        userId = store.userId,
                    )
                },
            )
        }
    }
}
