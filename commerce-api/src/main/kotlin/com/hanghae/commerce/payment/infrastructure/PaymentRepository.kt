package com.hanghae.commerce.payment.infrastructure

import com.hanghae.commerce.payment.domain.Payment

interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findById(id: String): Payment?
    fun findByOrderId(orderId: String): Payment?
    fun deleteAll()
}
