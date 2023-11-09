package com.hanghae.commerce.store.domain

import com.hanghae.commerce.common.IdentifierConstants

class Store(
    val id: String = IdentifierConstants.NOT_YET_PERSISTED_ID,
    val name: String,
    val userId: String,
)
