package com.hanghae.commerce.item.infrastructure

import com.hanghae.commerce.item.domain.Item
import org.springframework.stereotype.Component

@Component
class ItemReader(
    private val itemRepository: ItemRepository,
) {
    fun read(idList: List<String>): List<Item> {
        return itemRepository.findByIdIn(idList)
    }

    fun getItemsByStoreId(storeId: String): List<Item> {
        return itemRepository.findAllByStoreId(storeId)
    }

    fun getItemByItemId(itemId: String): Item? {
        return itemRepository.findById(itemId)
    }
}
