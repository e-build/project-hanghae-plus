package com.hanghae.commerce.payment.domain

import com.hanghae.commerce.claim.domain.OrderCancelCompletedEvent
import com.hanghae.commerce.order.infrastructure.OrderReader
import com.hanghae.commerce.payment.domain.command.PaymentRefundCommand
import com.hanghae.commerce.payment.infrastructure.PaymentReader
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class PaymentEventListener(
    private val paymentService: PaymentService,
    private val orderReader: OrderReader,
    private val paymentReader: PaymentReader,
) {
    @EventListener
    fun onOrderCanceled(event: OrderCancelCompletedEvent) {
        val payment = paymentReader.readByOrderId(event.orderId)
        payment ?: return
        payment.cancel()

        val order = orderReader.read(event.orderId)
        paymentService.refund(
            command = PaymentRefundCommand(
                orderId = order.id,
                payment = payment,
                bankAccount = event.bankAccount,
            ),
            order = order,
        )
    }
}
