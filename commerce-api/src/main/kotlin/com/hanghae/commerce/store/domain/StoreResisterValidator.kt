package com.hanghae.commerce.store.domain

import com.hanghae.commerce.store.infrastructure.StoreReader
import com.hanghae.commerce.user.domain.User
import org.springframework.stereotype.Component

@Component
class StoreResisterValidator(
    private val storeReader: StoreReader,
) {

    fun validate(user: User, store: Store) {
        if (user.isNotSeller()) {
            throw IllegalArgumentException("해당 유저는 판매자가 아닙니다.")
        }
        if (store.name.isBlank()) {
            throw IllegalArgumentException("이름은 비어 있을 수 없습니다")
        }
        if (storeReader.search(store.name).isNotEmpty()) {
            throw IllegalArgumentException("상점이름이 중복됩니다.")
        }
    }
}
