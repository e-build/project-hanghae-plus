package com.hanghae.commerce.user.domain

class User(
    val id: String,
    val name: String,
    val age: Int,
    val email: String,
    val address: String,
    val userType: UserType,
) {
    fun isNotSeller(): Boolean {
        return userType != UserType.SELLER
    }

    init {
        if (name.isBlank()) {
            throw IllegalArgumentException("이름은 비어 있을 수 없습니다")
        }
        if (email.isBlank()) {
            throw IllegalArgumentException("이메일은 비어 있을 수 없습니다")
        }
        if (address.isBlank()) {
            throw IllegalArgumentException("주소는 비어 있을 수 없습니다")
        }
    }
}
