package com.miraitowa.shiguang.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraitowa.shiguang.data.AllCategory
import com.miraitowa.shiguang.data.AllCategoryId
import com.miraitowa.shiguang.data.Category
import com.miraitowa.shiguang.data.InventoryItem
import com.miraitowa.shiguang.data.InventoryStore
import com.miraitowa.shiguang.data.itemsFor
import com.miraitowa.shiguang.ui.theme.Mist
import com.miraitowa.shiguang.ui.theme.MutedText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun InventoryApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val store = remember { InventoryStore(context) }
    val configuration = LocalConfiguration.current
    val isWide = configuration.screenWidthDp >= 760

    var selectedCategoryId by rememberSaveable { mutableStateOf(AllCategoryId) }
    var selectedItemId by rememberSaveable { mutableStateOf<String?>(null) }
    var detailItemId by rememberSaveable { mutableStateOf<String?>(null) }

    var showCreateMenu by rememberSaveable { mutableStateOf(false) }
    var showItemEditor by rememberSaveable { mutableStateOf(false) }
    var editingItemId by rememberSaveable { mutableStateOf<String?>(null) }
    var showCategoryEditor by rememberSaveable { mutableStateOf(false) }
    var editingCategoryId by rememberSaveable { mutableStateOf<String?>(null) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var showAppTitleEditor by rememberSaveable { mutableStateOf(false) }
    var multiSelectEnabled by rememberSaveable { mutableStateOf(false) }
    var selectedItemIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }

    val data = store.data
    val globalAccent = store.globalAccent
    val activeCategory = (listOf(AllCategory) + data.categories)
        .firstOrNull { it.id == selectedCategoryId } ?: AllCategory
    val effectiveAccent = globalAccent?.let { Color(it) } ?: Color(activeCategory.accent)
    val defaultCategoryId = when {
        activeCategory.id != AllCategoryId -> activeCategory.id
        else -> data.categories.firstOrNull()?.id ?: AllCategoryId
    }
    val editorItem = editingItemId?.let(store::itemById)
    val detailItem = detailItemId?.let(store::itemById)
    val detailCategory = detailItem?.let { item ->
        data.categories.firstOrNull { it.id == item.categoryId }
    }
    val detailIndex = detailItem?.let {
        data.itemsFor(selectedCategoryId).indexOfFirst { item -> item.id == it.id } + 1
    } ?: 1

    BackHandler(enabled = multiSelectEnabled) {
        multiSelectEnabled = false
        selectedItemIds = emptySet()
    }

    LaunchedEffect(data.categories.map { it.id }) {
        if (selectedCategoryId != AllCategoryId && data.categories.none { it.id == selectedCategoryId }) {
            selectedCategoryId = AllCategoryId
            selectedItemId = null
        }
    }

    LaunchedEffect(detailItemId, data.items.size) {
        if (detailItemId != null && store.itemById(detailItemId) == null) {
            detailItemId = null
        }
    }

    LaunchedEffect(data.items.size) {
        if (selectedItemIds.isNotEmpty()) {
            val existingIds = data.items.mapTo(mutableSetOf()) { it.id }
            selectedItemIds = selectedItemIds.intersect(existingIds)
        }
    }

    fun closeMultiSelect() {
        multiSelectEnabled = false
        selectedItemIds = emptySet()
    }

    fun openNewItem() {
        showCreateMenu = false
        editingItemId = null
        scope.launch {
            delay(120)
            showItemEditor = true
        }
    }

    fun openNewCategory() {
        showCreateMenu = false
        editingCategoryId = null
        scope.launch {
            delay(120)
            showCategoryEditor = true
        }
    }

    fun openEditorFor(item: InventoryItem) {
        detailItemId = null
        editingItemId = item.id
        scope.launch {
            delay(130)
            showItemEditor = true
        }
    }

    fun openCategoryEditor(category: Category) {
        if (category.id == AllCategoryId) return
        editingCategoryId = category.id
        showCategoryEditor = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        InventoryScreen(
            data = data,
            selectedCategoryId = selectedCategoryId,
            selectedItemId = selectedItemId,
            isWide = isWide,
            globalAccent = globalAccent,
            appTitle = store.appTitle,
            appSubtitle = store.appSubtitle,
            multiSelectEnabled = multiSelectEnabled,
            selectedItemIds = selectedItemIds,
            onCategorySelected = { categoryId ->
                selectedCategoryId = categoryId
                selectedItemId = null
                detailItemId = null
                closeMultiSelect()
            },
            onItemSelected = { item ->
                selectedItemId = item.id
                if (!isWide) {
                    detailItemId = item.id
                }
            },
            onEditCategory = { category ->
                openCategoryEditor(category)
            },
            onEditItem = { item ->
                openEditorFor(item)
            },
            onAdd = {
                closeMultiSelect()
                showCreateMenu = true
            },
            onNewItem = { openNewItem() },
            onSettings = { showSettings = true },
            onEditAppText = { showAppTitleEditor = true },
            onToggleMultiSelect = {
                if (multiSelectEnabled) {
                    closeMultiSelect()
                } else {
                    multiSelectEnabled = true
                    selectedItemIds = emptySet()
                    detailItemId = null
                }
            },
            onToggleItemSelection = { itemId ->
                selectedItemIds = if (itemId in selectedItemIds) {
                    selectedItemIds - itemId
                } else {
                    selectedItemIds + itemId
                }
            },
            onSelectionChange = { ids ->
                selectedItemIds = ids
            },
            onDeleteSelected = {
                if (selectedItemIds.isNotEmpty()) {
                    showBulkDeleteConfirm = true
                }
            },
        )
    }

    if (showCreateMenu) {
        CreateMenuSheet(
            accent = effectiveAccent,
            onDismiss = { showCreateMenu = false },
            onNewItem = { openNewItem() },
            onNewCategory = { openNewCategory() },
        )
    }

    if (showItemEditor) {
        ItemEditorSheet(
            item = editorItem,
            categories = data.categories,
            defaultCategoryId = defaultCategoryId,
            onDismiss = { showItemEditor = false },
            onSave = { saved ->
                store.upsertItem(saved)
                showItemEditor = false
                selectedItemId = saved.id
                if (selectedCategoryId != AllCategoryId && saved.categoryId != selectedCategoryId) {
                    selectedCategoryId = saved.categoryId
                }
                Toast.makeText(context, "已收进拾光匣", Toast.LENGTH_SHORT).show()
            },
            onDelete = { item ->
                store.deleteItem(item.id)
                showItemEditor = false
                if (selectedItemId == item.id) selectedItemId = null
                if (detailItemId == item.id) detailItemId = null
            },
        )
    }

    if (showCategoryEditor) {
        val category = editingCategoryId?.let(store::categoryById)
        val itemCount = category?.let { cat ->
            data.items.filter { it.categoryId == cat.id }.sumOf { it.quantity }
        } ?: 0
        CategoryEditorSheet(
            category = category,
            itemCount = itemCount,
            onDismiss = { showCategoryEditor = false },
            onSave = { saved ->
                store.upsertCategory(saved)
                showCategoryEditor = false
                selectedCategoryId = saved.id
                selectedItemId = null
                detailItemId = null
                Toast.makeText(context, "分类已整理好", Toast.LENGTH_SHORT).show()
            },
            onDelete = { categoryToDelete ->
                store.deleteCategory(categoryToDelete.id)
                showCategoryEditor = false
                selectedCategoryId = AllCategoryId
                selectedItemId = null
                detailItemId = null
            },
        )
    }

    if (!isWide && detailItem != null && detailCategory != null && !multiSelectEnabled) {
        ItemDetailSheet(
            item = detailItem,
            category = detailCategory,
            index = detailIndex.coerceAtLeast(1),
            onDismiss = { detailItemId = null },
            onEdit = { openEditorFor(detailItem) },
        )
    }

    if (showSettings) {
        SettingsSheet(
            currentAccent = globalAccent,
            fallbackAccent = activeCategory.accent,
            appTitle = store.appTitle,
            onDismiss = { showSettings = false },
            onSelect = { accent ->
                store.updateGlobalAccent(accent)
            },
        )
    }

    if (showAppTitleEditor) {
        AppTitleEditorSheet(
            currentTitle = store.appTitle,
            currentSubtitle = store.appSubtitle,
            accent = effectiveAccent,
            onDismiss = { showAppTitleEditor = false },
            onSave = { title, subtitle ->
                store.updateAppText(title, subtitle)
                showAppTitleEditor = false
                Toast.makeText(context, "标题已更新", Toast.LENGTH_SHORT).show()
            },
        )
    }

    if (showBulkDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteConfirm = false },
            title = {
                Text("移除所选物品？", color = Mist, fontWeight = FontWeight.SemiBold)
            },
            text = {
                Text(
                    "将移除所选的 ${selectedItemIds.size} 种物品记录。这个操作不会影响现实中的它们。",
                    color = MutedText,
                    fontSize = 13.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val idsToDelete = selectedItemIds
                    store.deleteItems(idsToDelete)
                    showBulkDeleteConfirm = false
                    closeMultiSelect()
                    if (selectedItemId in idsToDelete) selectedItemId = null
                    detailItemId = null
                    Toast.makeText(context, "已移除 ${idsToDelete.size} 种物品", Toast.LENGTH_SHORT).show()
                }) {
                    Text("移除", color = Color(0xFFFF9B92))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteConfirm = false }) {
                    Text("保留", color = Color.White.copy(alpha = 0.65f))
                }
            },
            containerColor = Color(0xFF102027),
            shape = RoundedCornerShape(26.dp),
        )
    }
}
