package com.hanghae.commerce.cart.api.dto

data class CartResponse(
    val cartId: String,
    val items: List<CartItemResponse>,
)
