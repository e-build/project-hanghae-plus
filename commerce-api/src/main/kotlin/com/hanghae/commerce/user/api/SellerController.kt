package com.hanghae.commerce.user.api

import com.hanghae.commerce.user.application.SellerWriterService
import com.hanghae.commerce.user.api.dto.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/seller")
class SellerController(
    private val sellerService: SellerWriterService,
) {

    @PostMapping
    fun createSeller(@RequestBody createSellerRequest: CreateSellerRequest): CreateSellerResponse {
        return sellerService.createSeller(createSellerRequest)
    }
}
