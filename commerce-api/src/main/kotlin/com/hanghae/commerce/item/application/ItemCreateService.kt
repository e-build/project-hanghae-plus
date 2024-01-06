package com.hanghae.commerce.item.application

import com.hanghae.commerce.item.domain.Item
import com.hanghae.commerce.item.infrastructure.ItemWriter
import com.hanghae.commerce.item.api.dto.CreateItemRequest
import com.hanghae.commerce.item.api.dto.CreateItemResponse
import com.hanghae.commerce.item.infrastructure.ItemReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ItemCreateService(
    private val itemWriter: ItemWriter,
    private val itemReader: ItemReader
) {

    @Transactional
    fun createItem(request: CreateItemRequest): CreateItemResponse {
        val item = Item.of(
            id = UUID.randomUUID().toString(),
            name = request.name,
            price = request.price,
            stock = request.stock,
            storeId = request.storeId,
        )

        val savedItem = itemWriter.save(item)
        modifyItem(savedItem.id)

        return CreateItemResponse.of(savedItem)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun modifyItem(itemId: String) {
        val item = itemReader.getItemByItemId(itemId)
        item?.let {
            it.name = "${item.name}_modified"
        }
    }
}
