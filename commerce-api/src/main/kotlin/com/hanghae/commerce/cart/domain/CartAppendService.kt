package com.hanghae.commerce.cart.domain

import com.hanghae.commerce.cart.domain.command.CartCommand
import com.hanghae.commerce.cart.infrastructure.CartReader
import com.hanghae.commerce.cart.infrastructure.CartWriter
import org.springframework.stereotype.Service

@Service
class CartAppendService(
    private val cartWriter: CartWriter,
    private val cartReader: CartReader,
) {
    fun appendItem(command: CartCommand.AddItem): CartItem {
        val cart = cartReader.readByUserId(command.userId)
            ?: cartWriter.write(Cart(userId = command.userId))

        cart.addItem(
            CartItem(
                itemId = command.itemId,
                quantity = command.quantity,
            ),
        )
        return cartWriter.write(cart)
            .find(command.itemId)!!
    }
}
