package com.hanghae.commerce.data.domain.cart

import com.hanghae.commerce.cart.domain.Cart
import com.hanghae.commerce.cart.domain.CartItem

fun CartEntity.toDomain(itemEntities: List<CartItemEntity>): Cart {
    return Cart(
        id = this.id,
        userId = this.userId,
        items = itemEntities.map { it.toDomain() }.toMutableList(),
    )
}

fun Cart.toEntity(): CartEntity {
    return CartEntity(
        identifier = this.id,
        userId = this.userId,
    )
}

fun CartItem.toEntity(userId: String): CartItemEntity {
    return CartItemEntity(
        identifier = this.id,
        itemId = this.itemId,
        quantity = this.quantity,
        cart = CartEntity(
            identifier = id,
            userId = userId,
        ),
    )
}

fun CartItemEntity.toDomain(): CartItem {
    return CartItem(
        id = this.id,
        itemId = this.itemId,
        quantity = this.quantity,
    )
}
