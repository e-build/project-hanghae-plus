package com.hanghae.commerce.cart.application

import com.hanghae.commerce.cart.domain.CartReaderService
import com.hanghae.commerce.cart.domain.CartAppendService
import com.hanghae.commerce.cart.api.dto.AddCartItemRequest
import com.hanghae.commerce.cart.api.dto.toCommand
import com.hanghae.commerce.testconfiguration.IntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
class CartAppendServiceTest(
    @Autowired private val cartReaderService: CartReaderService,
    @Autowired private val cartAppendService: CartAppendService,
) {

    @Nested
    @DisplayName("장바구니에")
    internal inner class when_a_product_is_added {
        private var itemQuantity: Int? = 0
        private var cartSize: Int? = 0

        @BeforeEach
        fun setUp() {
            val request = AddCartItemRequest(
                userId = "1",
                quantity = 1,
                itemId = "item_id1",
            )
            itemQuantity = cartAppendService.appendItem(request.toCommand()).quantity

            cartSize = cartReaderService.getCart("1").items.size
        }

        @Test
        @DisplayName("같은 상품을 추가하면 수량+1이 된다.")
        fun addItem() {
            val request = AddCartItemRequest(
                userId = "1",
                quantity = 1,
                itemId = "item_id1",
            )
            val cartItem = cartAppendService.appendItem(request.toCommand())

            assertThat(cartItem.quantity).isEqualTo(itemQuantity?.plus(1))
        }

        @Test
        @DisplayName("새로운 상품을 추가하면 추가된다.")
        fun addNewItem() {
            assertThat(cartReaderService.getCart("1").items).isEqualTo(cartSize?.plus(1))
        }
    }
}
