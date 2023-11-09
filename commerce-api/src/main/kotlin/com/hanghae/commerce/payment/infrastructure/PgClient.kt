package com.hanghae.commerce.payment.infrastructure

import com.hanghae.commerce.payment.domain.BankAccount
import com.hanghae.commerce.payment.domain.Payment

interface PgClient {

    fun payment(payment: Payment)
    fun refund(payment: Payment)
    fun validateAccount(bankAccount: BankAccount): Boolean
}
