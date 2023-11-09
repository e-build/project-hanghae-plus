package com.hanghae.commerce.cart.domain

import com.hanghae.commerce.cart.infrastructure.CartReader
import org.springframework.stereotype.Service

@Service
class CartReaderService(
    private val cartReader: CartReader,
) {

    fun getCart(userId: String): Cart {
        return cartReader.readByUserId(userId) ?: Cart(userId = userId)
    }
}
