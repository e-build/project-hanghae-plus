package com.hanghae.commerce.order.api.dto

data class OrderRequest(
    val userId: String,
    val itemList: List<Item>,
) {
    data class Item(
        val id: String,
        val quantity: Int,
    )
}
