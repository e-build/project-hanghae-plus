package com.hanghae.commerce.cart.domain.command

class CartCommand {

    data class AddItem(
        val userId: String,
        val itemId: String,
        val quantity: Int,
    )

    data class ModifyItemQuantity(
        val cartId: String,
        val itemId: String,
        val quantity: Int,
    )
}
