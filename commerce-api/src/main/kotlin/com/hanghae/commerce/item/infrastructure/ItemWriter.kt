package com.hanghae.commerce.item.infrastructure

import com.hanghae.commerce.item.domain.Item
import org.springframework.stereotype.Component

@Component
class ItemWriter(
    private val itemRepository: ItemRepository,
) {
    fun save(item: Item): Item {
        return itemRepository.save(item)
    }

    fun allDelete() {
        itemRepository.deleteAll()
    }
}
