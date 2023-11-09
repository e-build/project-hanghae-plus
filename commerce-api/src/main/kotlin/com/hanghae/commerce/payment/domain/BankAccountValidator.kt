package com.hanghae.commerce.payment.domain

import com.hanghae.commerce.payment.infrastructure.PgClient
import org.springframework.stereotype.Component

@Component
class BankAccountValidator(
    private val pgClient: PgClient,
) {
    fun validate(bankAccount: BankAccount): Boolean {
        return pgClient.validateAccount(bankAccount)
    }
}
