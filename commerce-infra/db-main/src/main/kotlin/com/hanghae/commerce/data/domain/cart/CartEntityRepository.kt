package com.hanghae.commerce.data.domain.cart

import com.hanghae.commerce.cart.domain.Cart
import com.hanghae.commerce.cart.infrastructure.CartRepository
import org.springframework.stereotype.Repository

@Repository
class CartEntityRepository(
    private val jpaCartRepository: JpaCartRepository,
    private val jpaCartItemRepository: JpaCartItemRepository,
) : CartRepository {

    override fun findById(cartId: String): Cart? {
        return jpaCartRepository.findById(cartId).orElse(null)
            ?.let {
                it.toDomain(jpaCartItemRepository.findAllByCartId(it.id))
            }
    }

    override fun findByUserId(userId: String): Cart? {
        return jpaCartRepository.findByUserId(userId)
            ?.let {
                Cart(
                    id = it.id,
                    userId = it.userId,
                )
            }
    }

    override fun save(cart: Cart): Cart {
        val cartEntity = jpaCartRepository.save(cart.toEntity())
        return Cart(id = cartEntity.id, userId = cartEntity.userId)
    }
}
