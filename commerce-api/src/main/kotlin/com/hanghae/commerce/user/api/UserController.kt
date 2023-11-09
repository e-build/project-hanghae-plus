package com.hanghae.commerce.user.api

import com.hanghae.commerce.user.application.UserReaderService
import com.hanghae.commerce.user.api.dto.GetUserResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserReaderService,
) {

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: String): GetUserResponse {
        return userService.getUserById(userId)
    }
}
