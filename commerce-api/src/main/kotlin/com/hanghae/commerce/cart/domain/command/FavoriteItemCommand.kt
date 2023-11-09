package com.hanghae.commerce.cart.domain.command

class FavoriteItemCommand {

    data class Add(
        val itemId: String,
        val userId: String,
    )
}
