package com.domain.entity

data class OrderProduct(
        val title: String,
        val productVariant: ProductVariant,
        val quantity: Int
)