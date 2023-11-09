package com.hanghae.commerce.cart.infrastructure

import com.hanghae.commerce.cart.domain.Cart

interface CartRepository {
    fun findById(cartId: String): Cart?
    fun findByUserId(userId: String): Cart?
    fun save(cart: Cart): Cart
}
