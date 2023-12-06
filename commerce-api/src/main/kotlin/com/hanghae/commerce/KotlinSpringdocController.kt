package com.hanghae.commerce

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/springdoc-test/kotlin")
class KotlinSpringdocController {
    @PostMapping
    fun post(
        @RequestParam name: String,
        @RequestParam(required = false) point: Double,
        @RequestParam(required = false) age: Int,
        @RequestParam(required = false) boolValue: Boolean,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("success")
    }
}
