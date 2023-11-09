package com.hanghae.commerce.cart.api.dto

class ModifyCartItemQuantityRequest(
    val cartId: String,
    val itemId: String,
    val quantity: Int = 1,
)
