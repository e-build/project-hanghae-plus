package com.hanghae.commerce.item.api.dto

data class CreateItemRequest(
    val storeId: String,
    val name: String,
    val price: Int,
    val stock: Long,
)
