package com.hanghae.commerce.cart.domain

import com.hanghae.commerce.common.IdentifierConstants

class Cart(
    val id: String = IdentifierConstants.NOT_YET_PERSISTED_ID,
    val userId: String,
    val items: MutableList<CartItem> = mutableListOf(),
) {
    fun addItem(cartItem: CartItem) {
        this.items.add(cartItem)
    }

    fun find(itemId: String): CartItem? {
        return this.items.firstOrNull { it.itemId == itemId }
    }

    fun modifyItemQuantity(itemId: String, quantity: Int) {
        find(itemId).run { this?.quantity = quantity }
    }
}
