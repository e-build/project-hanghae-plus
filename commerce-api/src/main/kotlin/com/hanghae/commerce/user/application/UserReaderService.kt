package com.hanghae.commerce.user.application

import com.hanghae.commerce.user.domain.UserReader
import com.hanghae.commerce.user.api.dto.GetUserResponse
import org.springframework.stereotype.Service

@Service
class UserReaderService(
    private val userReader: UserReader,
) {

    fun getUserById(userId: String): GetUserResponse {
        return GetUserResponse.of(
            userReader.findById(userId),
        )
    }
}
