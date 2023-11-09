package com.hanghae.commerce.cart.api

import com.hanghae.commerce.cart.application.CartFacade
import com.hanghae.commerce.cart.api.dto.*
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/carts")
class CartController(
    private val cartFacade: CartFacade,
) {

    @GetMapping("/users/{userId}")
    fun getCartByUserId(@PathVariable userId: String): CartResponse {
        return cartFacade.get(userId)
            .let {
                CartResponse(
                    cartId = it.id,
                    items = it.items.map { cartItem ->
                        CartItemResponse(
                            id = cartItem.id,
                            itemId = cartItem.itemId,
                            quantity = cartItem.quantity,
                        )
                    },
                )
            }
    }

    @PostMapping("/add-item")
    fun addCartItem(
        @RequestBody @Valid
        addCartItemRequest: AddCartItemRequest,
    ): CartItemResponse {
        return cartFacade.addItem(addCartItemRequest.toCommand())
            .let {
                CartItemResponse(
                    id = it.id,
                    itemId = it.itemId,
                    quantity = it.quantity,
                )
            }
    }

    @PatchMapping("/cart-item")
    fun updateCartItemQuantity(
        @RequestBody @Valid
        modifyCartItemQuantityRequest: ModifyCartItemQuantityRequest,
    ): CartItemResponse? {
        return cartFacade.updateItemQuantity(modifyCartItemQuantityRequest.toCommand())
            .let {
                CartItemResponse(
                    id = it.id,
                    itemId = it.itemId,
                    quantity = it.quantity,
                )
            }
    }
}
