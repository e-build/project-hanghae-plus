package com.hanghae.commerce.cart.api.dto

import com.hanghae.commerce.cart.domain.command.CartCommand

fun AddCartItemRequest.toCommand(): CartCommand.AddItem {
    return CartCommand.AddItem(
        userId = userId,
        itemId = itemId,
        quantity = quantity,
    )
}

fun ModifyCartItemQuantityRequest.toCommand(): CartCommand.ModifyItemQuantity {
    return CartCommand.ModifyItemQuantity(
        cartId = cartId,
        itemId = itemId,
        quantity = quantity,
    )
}
