package com.hanghae.commerce.payment.infrastructure

import com.hanghae.commerce.payment.domain.Payment
import org.springframework.stereotype.Component

@Component
class PaymentWriter(
    private val paymentRepository: PaymentRepository,
) {

    fun write(payment: Payment): Payment {
        return paymentRepository.save(payment)
    }
}
