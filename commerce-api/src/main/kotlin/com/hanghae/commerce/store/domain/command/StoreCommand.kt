package com.hanghae.commerce.store.domain.command

class StoreCommand {

    data class Resister(
        val userId: String,
        val name: String,
    )
}
