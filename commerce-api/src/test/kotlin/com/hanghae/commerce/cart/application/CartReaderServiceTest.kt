package com.hanghae.commerce.cart.application

import com.hanghae.commerce.cart.domain.CartReaderService
import com.hanghae.commerce.cart.domain.CartAppendService
import com.hanghae.commerce.cart.api.dto.AddCartItemRequest
import com.hanghae.commerce.cart.api.dto.toCommand
import com.hanghae.commerce.testconfiguration.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
class CartReaderServiceTest(
    @Autowired private val cartReaderService: CartReaderService,
    @Autowired private val cartAppendService: CartAppendService,
) {

    @BeforeEach
    fun setUp() {
        val request = AddCartItemRequest(
            userId = "1",
            quantity = 1,
            itemId = "item_id1",
        )
        cartAppendService.appendItem(request.toCommand())
    }

    @Test
    @DisplayName("유저 장바구니 목록 조회한다")
    fun getCartItemsByUserId() {
        val len = cartReaderService.getCart(userId = "1").items.size

        val request = AddCartItemRequest(
            userId = "1",
            quantity = 1,
            itemId = "item_id2",
        )
        cartAppendService.appendItem(request.toCommand())

        assertThat(cartReaderService.getCart(userId = "1").items.size).isEqualTo(len + 1)
    }
}
