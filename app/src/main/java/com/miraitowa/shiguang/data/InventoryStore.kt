package com.miraitowa.shiguang.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class InventoryStore(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences("shiguang_inventory", Context.MODE_PRIVATE)

    var data by mutableStateOf(load())
        private set

    var globalAccent by mutableStateOf(loadGlobalAccent())
        private set

    var appTitle by mutableStateOf(prefs.getString(KEY_APP_TITLE, DEFAULT_APP_TITLE) ?: DEFAULT_APP_TITLE)
        private set

    var appSubtitle by mutableStateOf(prefs.getString(KEY_APP_SUBTITLE, DEFAULT_APP_SUBTITLE) ?: DEFAULT_APP_SUBTITLE)
        private set

    private fun loadGlobalAccent(): Long? =
        if (prefs.contains(KEY_GLOBAL_ACCENT)) prefs.getLong(KEY_GLOBAL_ACCENT, 0L) else null

    fun updateGlobalAccent(accent: Long?) {
        globalAccent = accent
        prefs.edit().apply {
            if (accent == null) remove(KEY_GLOBAL_ACCENT) else putLong(KEY_GLOBAL_ACCENT, accent)
        }.apply()
    }

    fun updateAppText(title: String, subtitle: String) {
        appTitle = title.trim().ifBlank { DEFAULT_APP_TITLE }
        appSubtitle = subtitle.trim()
        prefs.edit()
            .putString(KEY_APP_TITLE, appTitle)
            .putString(KEY_APP_SUBTITLE, appSubtitle)
            .apply()
    }

    private fun load(): InventoryData {
        val raw = prefs.getString(KEY_DATA, null) ?: return seedInventory().also { seed ->
            prefs.edit().putString(KEY_DATA, encode(seed).toString()).apply()
        }
        return runCatching { decode(JSONObject(raw)) }.getOrElse { seedInventory() }
    }

    private fun persist() {
        prefs.edit().putString(KEY_DATA, encode(data).toString()).apply()
    }

    fun itemById(id: String?): InventoryItem? = data.items.firstOrNull { it.id == id }

    fun categoryById(id: String?): Category? = data.categories.firstOrNull { it.id == id }

    fun upsertItem(item: InventoryItem) {
        val existing = itemById(item.id)
        val items = if (existing == null) {
            listOf(item) + data.items
        } else {
            data.items.map { if (it.id == item.id) item else it }
        }
        if (existing?.customIconPath != null && existing.customIconPath != item.customIconPath) {
            deleteImageIfUnused(existing.customIconPath, items = items)
        }
        data = data.copy(items = items)
        persist()
    }

    fun deleteItem(itemId: String) {
        deleteItems(setOf(itemId))
    }

    fun deleteItems(itemIds: Set<String>) {
        if (itemIds.isEmpty()) return
        val removed = data.items.filter { it.id in itemIds }
        val items = data.items.filterNot { it.id in itemIds }
        removed.forEach { item ->
            if (item.customIconPath != null) {
                deleteImageIfUnused(item.customIconPath, items = items)
            }
        }
        data = data.copy(items = items)
        persist()
    }

    fun upsertCategory(category: Category) {
        val existing = categoryById(category.id)
        val categories = if (existing == null) {
            data.categories + category
        } else {
            data.categories.map { if (it.id == category.id) category else it }
        }.sortedBy { it.order }
        if (existing?.customIconPath != null && existing.customIconPath != category.customIconPath) {
            deleteImageIfUnused(existing.customIconPath, categories = categories)
        }
        data = data.copy(categories = categories)
        persist()
    }

    fun deleteCategory(categoryId: String) {
        if (categoryId == AllCategoryId) return
        val category = categoryById(categoryId) ?: return
        var categories = data.categories.filterNot { it.id == categoryId }
        val fallback = categories.firstOrNull { it.name == "其他" }
            ?: Category(
                id = "other_" + UUID.randomUUID().toString().take(6),
                name = "其他",
                iconKey = "ic_boxes",
                accent = 0xFFA5B2C2,
                order = categories.size,
            ).also { categories = categories + it }

        val items = data.items.map {
            if (it.categoryId == categoryId) it.copy(categoryId = fallback.id) else it
        }
        if (category.customIconPath != null) {
            deleteImageIfUnused(category.customIconPath, items = items, categories = categories)
        }
        data = data.copy(categories = categories, items = items)
        persist()
    }

    fun updateQuantity(itemId: String, quantity: Int) {
        val item = itemById(itemId) ?: return
        upsertItem(item.copy(quantity = quantity.coerceAtLeast(0), updatedAt = System.currentTimeMillis()))
    }

    fun resetToSeed() {
        data = seedInventory()
        persist()
    }

    private fun deleteImageIfUnused(
        path: String,
        items: List<InventoryItem> = data.items,
        categories: List<Category> = data.categories,
    ) {
        val stillUsed = items.any { it.customIconPath == path } || categories.any { it.customIconPath == path }
        if (!stillUsed) {
            runCatching { File(path).delete() }
        }
    }

    private fun encode(data: InventoryData): JSONObject {
        val categories = JSONArray()
        data.categories.forEach { category ->
            categories.put(JSONObject().apply {
                put("id", category.id)
                put("name", category.name)
                put("iconKey", category.iconKey)
                put("customIconPath", category.customIconPath ?: JSONObject.NULL)
                put("accent", category.accent)
                put("order", category.order)
            })
        }
        val items = JSONArray()
        data.items.forEach { item ->
            items.put(JSONObject().apply {
                put("id", item.id)
                put("categoryId", item.categoryId)
                put("name", item.name)
                put("note", item.note)
                put("location", item.location)
                put("quantity", item.quantity)
                put("iconKey", item.iconKey)
                put("customIconPath", item.customIconPath ?: JSONObject.NULL)
                put("accent", item.accent)
                put("createdAt", item.createdAt)
                put("updatedAt", item.updatedAt)
            })
        }
        return JSONObject().apply {
            put("version", 1)
            put("categories", categories)
            put("items", items)
        }
    }

    private fun decode(root: JSONObject): InventoryData {
        val categories = mutableListOf<Category>()
        val categoryArray = root.optJSONArray("categories") ?: JSONArray()
        for (i in 0 until categoryArray.length()) {
            val obj = categoryArray.optJSONObject(i) ?: continue
            val id = obj.optString("id")
            if (id.isBlank() || id == AllCategoryId) continue
            categories += Category(
                id = id,
                name = obj.optString("name", "未命名分类"),
                iconKey = obj.optString("iconKey", "ic_boxes"),
                customIconPath = if (obj.isNull("customIconPath")) null else obj.optString("customIconPath").takeIf { it.isNotBlank() },
                accent = obj.optLong("accent", 0xFF6FE7D2),
                order = obj.optInt("order", i),
            )
        }
        val safeCategories = if (categories.isEmpty()) seedInventory().categories else categories.sortedBy { it.order }
        val categoryIds = safeCategories.map { it.id }.toSet()

        val items = mutableListOf<InventoryItem>()
        val itemArray = root.optJSONArray("items") ?: JSONArray()
        for (i in 0 until itemArray.length()) {
            val obj = itemArray.optJSONObject(i) ?: continue
            val id = obj.optString("id")
            if (id.isBlank()) continue
            val categoryId = obj.optString("categoryId").takeIf { it in categoryIds } ?: safeCategories.first().id
            items += InventoryItem(
                id = id,
                categoryId = categoryId,
                name = obj.optString("name", "未命名物品"),
                note = obj.optString("note"),
                location = obj.optString("location"),
                quantity = obj.optInt("quantity", 1).coerceAtLeast(0),
                iconKey = obj.optString("iconKey", "ic_package"),
                customIconPath = if (obj.isNull("customIconPath")) null else obj.optString("customIconPath").takeIf { it.isNotBlank() },
                accent = obj.optLong("accent", 0xFF6FE7D2),
                createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                updatedAt = obj.optLong("updatedAt", System.currentTimeMillis()),
            )
        }
        return InventoryData(categories = safeCategories, items = items)
    }

    companion object {
        private const val KEY_DATA = "data_v1"
        private const val KEY_GLOBAL_ACCENT = "global_accent_v1"
        private const val KEY_APP_TITLE = "app_title_v1"
        private const val KEY_APP_SUBTITLE = "app_subtitle_v1"
        private const val DEFAULT_APP_TITLE = "拾光匣"
        private const val DEFAULT_APP_SUBTITLE = "A LITTLE LIFE ARCHIVE"
    }
}

fun newItemId(): String = "item_" + UUID.randomUUID().toString().replace("-", "").take(12)
fun newCategoryId(): String = "cat_" + UUID.randomUUID().toString().replace("-", "").take(10)
