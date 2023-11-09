package com.hanghae.commerce.cart.api.dto

data class CartItemResponse(
    val id: String,
    val itemId: String,
    val quantity: Int,
)
