package com.hanghae.commerce.cart.application

import com.hanghae.commerce.cart.domain.*
import com.hanghae.commerce.cart.domain.command.CartCommand
import org.springframework.stereotype.Component

@Component
class CartFacade(
    private val cartAppendService: CartAppendService,
    private val cartModifyQuantityService: CartModifyQuantityService,
    private val cartReaderService: CartReaderService,
) {
    fun addItem(command: CartCommand.AddItem): CartItem {
        return cartAppendService.appendItem(command)
    }

    fun updateItemQuantity(command: CartCommand.ModifyItemQuantity): CartItem {
        return cartModifyQuantityService.modify(command)
    }

    fun get(userId: String): Cart {
        return cartReaderService.getCart(userId)
    }
}
