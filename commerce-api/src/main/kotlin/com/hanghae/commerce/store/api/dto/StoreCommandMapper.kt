package com.hanghae.commerce.store.api.dto

import com.hanghae.commerce.store.domain.command.StoreCommand

fun CreateStoreRequest.toCommand(): StoreCommand.Resister {
    return StoreCommand.Resister(
        userId = this.userId,
        name = this.name,
    )
}
