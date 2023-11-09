package com.hanghae.commerce.cart.infrastructure

import com.hanghae.commerce.cart.domain.Cart
import org.springframework.stereotype.Component

@Component
class CartReader(
    private val cartRepository: CartRepository,
) {

    fun readByUserId(userId: String): Cart? {
        return cartRepository.findByUserId(userId)
    }

    fun read(cartId: String): Cart {
        return cartRepository.findById(cartId)
            ?: throw IllegalArgumentException("Cart Not Found")
    }
}
