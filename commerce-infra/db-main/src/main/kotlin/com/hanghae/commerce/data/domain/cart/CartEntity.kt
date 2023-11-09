package com.hanghae.commerce.data.domain.cart

import com.hanghae.commerce.data.common.PrimaryKeyEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "cart",
    uniqueConstraints = [
        UniqueConstraint(
            name = "UniqueUser",
            columnNames = ["user_id"],
        ),
    ],
)
class CartEntity(
    @Transient
    private val identifier: String,

    @Column(name = "user_id", nullable = false, unique = true)
    val userId: String,
) : PrimaryKeyEntity(identifier)
