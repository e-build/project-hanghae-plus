package com.hanghae.commerce.cart.api.dto

data class AddCartItemRequest(
    val itemId: String,
    val quantity: Int,
    val userId: String,
)
