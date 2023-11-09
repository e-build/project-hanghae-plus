package com.hanghae.commerce.order.infrastructure

import com.hanghae.commerce.order.domain.Order

interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: String): Order?
    fun deleteAll()
}
