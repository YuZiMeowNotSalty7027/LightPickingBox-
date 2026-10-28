package com.miraitowa.shiguang.data

import androidx.compose.runtime.Immutable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Immutable
data class Category(
    val id: String,
    val name: String,
    val iconKey: String,
    val customIconPath: String? = null,
    val accent: Long,
    val order: Int = 0,
)

@Immutable
data class InventoryItem(
    val id: String,
    val categoryId: String,
    val name: String,
    val note: String,
    val location: String,
    val quantity: Int,
    val iconKey: String,
    val customIconPath: String? = null,
    val accent: Long,
    val createdAt: Long,
    val updatedAt: Long,
)

@Immutable
data class InventoryData(
    val categories: List<Category> = emptyList(),
    val items: List<InventoryItem> = emptyList(),
)

val AllCategoryId: String = "all"

val AllCategory: Category = Category(
    id = AllCategoryId,
    name = "全部物品",
    iconKey = "ic_grid",
    accent = 0xFF6FE7D2,
    order = -1,
)

fun InventoryItem.categoryOf(data: InventoryData): Category? =
    data.categories.firstOrNull { it.id == categoryId }

fun InventoryItem.createdDateLabel(): String {
    val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.CHINA)
    return formatter.format(Date(createdAt))
}

fun InventoryData.totalQuantity(): Int = items.sumOf { it.quantity }

fun InventoryData.totalKinds(): Int = items.size

fun InventoryData.itemsFor(categoryId: String?): List<InventoryItem> {
    val filtered = if (categoryId == null || categoryId == AllCategoryId) {
        items
    } else {
        items.filter { it.categoryId == categoryId }
    }
    return filtered.sortedByDescending { it.createdAt }
}

fun InventoryData.countFor(categoryId: String): Int {
    if (categoryId == AllCategoryId) return items.sumOf { it.quantity }
    return items.filter { it.categoryId == categoryId }.sumOf { it.quantity }
}
