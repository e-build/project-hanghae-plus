package com.hanghae.commerce.cart.domain

import com.hanghae.commerce.common.IdentifierConstants

class CartItem(
    val id: String = IdentifierConstants.NOT_YET_PERSISTED_ID,
    val itemId: String,
    var quantity: Int = 1,
)
