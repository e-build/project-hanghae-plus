package com.hanghae.commerce.data.domain.cart

import com.hanghae.commerce.data.common.PrimaryKeyEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "cart_item",
    uniqueConstraints = [
        UniqueConstraint(
            name = "UniqueCartAndItem",
            columnNames = ["cart_id", "item_id"],
        ),
    ],
)
class CartItemEntity(
    @Transient
    private val identifier: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    val cart: CartEntity,

    @Column(name = "item_id", nullable = false)
    val itemId: String,

    @Column(name = "quantity", nullable = false)
    var quantity: Int = 1,
) : PrimaryKeyEntity(identifier)
