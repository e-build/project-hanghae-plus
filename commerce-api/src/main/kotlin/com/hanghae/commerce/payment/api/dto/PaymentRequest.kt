package com.hanghae.commerce.payment.api.dto

import com.hanghae.commerce.payment.domain.BankAccount

data class PaymentRequest(
    val orderId: String,
    val bankAccount: BankAccount,
)
