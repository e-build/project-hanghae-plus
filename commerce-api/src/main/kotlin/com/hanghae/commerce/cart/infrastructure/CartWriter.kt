package com.hanghae.commerce.cart.infrastructure

import com.hanghae.commerce.cart.domain.Cart
import org.springframework.stereotype.Component

@Component
class CartWriter(
    private val cartRepository: CartRepository,
) {
    fun write(cart: Cart): Cart {
        return cartRepository.save(cart)
    }
}
