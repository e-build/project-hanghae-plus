package com.hanghae.commerce.cart.domain

import com.hanghae.commerce.cart.domain.command.CartCommand
import com.hanghae.commerce.cart.infrastructure.CartReader
import com.hanghae.commerce.cart.infrastructure.CartWriter
import org.springframework.stereotype.Service

@Service
class CartModifyQuantityService(
    private val cartReader: CartReader,
    private val cartWriter: CartWriter,
) {
    fun modify(command: CartCommand.ModifyItemQuantity): CartItem {
        val cart = cartReader.read(command.cartId)
        cart.modifyItemQuantity(command.itemId, command.quantity)
        return cartWriter.write(cart)
            .find(command.itemId)!!
    }
}
