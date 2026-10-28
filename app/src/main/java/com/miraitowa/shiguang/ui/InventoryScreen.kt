package com.miraitowa.shiguang.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraitowa.shiguang.data.AllCategory
import com.miraitowa.shiguang.data.AllCategoryId
import com.miraitowa.shiguang.data.Category
import com.miraitowa.shiguang.data.InventoryData
import com.miraitowa.shiguang.data.InventoryItem
import com.miraitowa.shiguang.data.countFor
import com.miraitowa.shiguang.data.createdDateLabel
import com.miraitowa.shiguang.data.itemsFor
import com.miraitowa.shiguang.data.totalKinds
import com.miraitowa.shiguang.data.totalQuantity
import com.miraitowa.shiguang.ui.components.AccentButton
import com.miraitowa.shiguang.ui.components.AuroraBackdrop
import com.miraitowa.shiguang.ui.components.CategoryGlyph
import com.miraitowa.shiguang.ui.components.EmptyState
import com.miraitowa.shiguang.ui.components.GhostButton
import com.miraitowa.shiguang.ui.components.GlassPanel
import com.miraitowa.shiguang.ui.components.GlyphIcon
import com.miraitowa.shiguang.ui.components.LocalImage
import com.miraitowa.shiguang.ui.components.RoundIconButton
import com.miraitowa.shiguang.ui.components.SectionLabel
import com.miraitowa.shiguang.ui.theme.Ink
import com.miraitowa.shiguang.ui.theme.Mist
import com.miraitowa.shiguang.ui.theme.MutedText

@Composable
fun InventoryScreen(
    data: InventoryData,
    selectedCategoryId: String,
    selectedItemId: String?,
    isWide: Boolean,
    globalAccent: Long?,
    appTitle: String,
    appSubtitle: String,
    multiSelectEnabled: Boolean,
    selectedItemIds: Set<String>,
    onCategorySelected: (String) -> Unit,
    onItemSelected: (InventoryItem) -> Unit,
    onEditCategory: (Category) -> Unit,
    onEditItem: (InventoryItem) -> Unit,
    onAdd: () -> Unit,
    onNewItem: () -> Unit,
    onSettings: () -> Unit,
    onEditAppText: () -> Unit,
    onToggleMultiSelect: () -> Unit,
    onToggleItemSelection: (String) -> Unit,
    onSelectionChange: (Set<String>) -> Unit,
    onDeleteSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = remember(data.categories) { listOf(AllCategory) + data.categories }
    val compactDetail = LocalConfiguration.current.screenHeightDp < 560
    val activeCategory = categories.firstOrNull { it.id == selectedCategoryId } ?: AllCategory
    val filteredItems = data.itemsFor(activeCategory.id)
    val activeItem = filteredItems.firstOrNull { it.id == selectedItemId } ?: filteredItems.firstOrNull()
    val activeItemCategory = activeItem?.let { item ->
        data.categories.firstOrNull { it.id == item.categoryId }
    } ?: activeCategory
    val rawAccent = globalAccent?.let { Color(it) } ?: Color(activeCategory.accent)
    val accent by animateColorAsState(rawAccent, tween(500), label = "categoryAccent")

    AuroraBackdrop(accent = accent, modifier = modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = if (isWide) 24.dp else 16.dp)
            ) {
                TopBar(
                    accent = accent,
                    appTitle = appTitle,
                    appSubtitle = appSubtitle,
                    totalQuantity = data.totalQuantity(),
                    totalKinds = data.totalKinds(),
                    multiSelectEnabled = multiSelectEnabled,
                    onAdd = onAdd,
                    onSettings = onSettings,
                    onEditAppText = onEditAppText,
                    onToggleMultiSelect = onToggleMultiSelect,
                )
                Spacer(Modifier.height(10.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = if (isWide) 24.dp else 12.dp, end = if (isWide) 24.dp else 12.dp)
            ) {
                CategoryRail(
                    categories = categories,
                    activeCategoryId = activeCategory.id,
                    accent = accent,
                    data = data,
                    onCategorySelected = onCategorySelected,
                    onEditCategory = onEditCategory,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 22.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    CategoryHeader(
                        category = activeCategory,
                        items = filteredItems,
                        accent = accent,
                        isWide = isWide,
                        multiSelectEnabled = multiSelectEnabled,
                        onEditCategory = { onEditCategory(activeCategory) },
                    )
                    AnimatedVisibility(
                        visible = multiSelectEnabled && filteredItems.isNotEmpty(),
                        enter = fadeIn(tween(180)),
                        exit = fadeOut(tween(120)),
                    ) {
                        Column {
                            Spacer(Modifier.height(10.dp))
                            MultiSelectBar(
                                selectedCount = selectedItemIds.size,
                                totalCount = filteredItems.size,
                                accent = accent,
                                onSelectAll = {
                                    onSelectionChange(filteredItems.map { it.id }.toSet())
                                },
                                onClearSelection = {
                                    onSelectionChange(emptySet())
                                },
                                onDelete = onDeleteSelected,
                                onExit = onToggleMultiSelect,
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    if (filteredItems.isEmpty()) {
                        EmptyState(
                            title = "这一格还空着",
                            subtitle = "把第一件真实存在的物品，收进你的拾光匣。",
                            accent = accent,
                            buttonText = "放入第一件物品",
                            onButtonClick = onNewItem,
                            icon = "ic_sparkles",
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(if (isWide) 5 else 4),
                            horizontalArrangement = Arrangement.spacedBy(9.dp),
                            verticalArrangement = Arrangement.spacedBy(9.dp),
                            contentPadding = PaddingValues(bottom = 28.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .navigationBarsPadding(),
                        ) {
                            items(
                                items = filteredItems,
                                key = { it.id },
                            ) { item ->
                                val selectedForMode = if (multiSelectEnabled) {
                                    item.id in selectedItemIds
                                } else {
                                    item.id == activeItem?.id
                                }
                                InventoryItemCard(
                                    item = item,
                                    selected = selectedForMode,
                                    multiSelectMode = multiSelectEnabled,
                                    accent = accent,
                                    onClick = {
                                        if (multiSelectEnabled) {
                                            onToggleItemSelection(item.id)
                                        } else {
                                            onItemSelected(item)
                                        }
                                    },
                                    onLongClick = {
                                        if (multiSelectEnabled) {
                                            onToggleItemSelection(item.id)
                                        } else {
                                            onEditItem(item)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
                if (isWide && !multiSelectEnabled) {
                    Spacer(Modifier.width(16.dp))
                    AnimatedVisibility(
                        visible = activeItem != null,
                        enter = fadeIn(tween(220)) + slideInHorizontally(tween(260)) { it / 5 },
                        exit = fadeOut(tween(140)),
                    ) {
                        activeItem?.let { item ->
                            ItemDetailPanel(
                                item = item,
                                category = activeItemCategory,
                                index = filteredItems.indexOf(item) + 1,
                                compact = compactDetail,
                                modifier = Modifier
                                    .width(326.dp)
                                    .fillMaxHeight()
                                    .navigationBarsPadding(),
                                onEdit = { onEditItem(item) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopBar(
    accent: Color,
    appTitle: String,
    appSubtitle: String,
    totalQuantity: Int,
    totalKinds: Int,
    multiSelectEnabled: Boolean,
    onAdd: () -> Unit,
    onSettings: () -> Unit,
    onEditAppText: () -> Unit,
    onToggleMultiSelect: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .combinedClickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onEditAppText()
                    },
                    onLongClickLabel = "自定义标题",
                    role = androidx.compose.ui.semantics.Role.Button,
                )
                .padding(vertical = 4.dp)
                .semantics { contentDescription = "长按自定义标题" },
        ) {
            Text(
                text = appTitle.ifBlank { "拾光匣" },
                color = Mist,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (appSubtitle.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = appSubtitle,
                    color = Color.White.copy(alpha = 0.34f),
                    fontSize = 9.sp,
                    letterSpacing = 1.1.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        CountPill(totalQuantity = totalQuantity, totalKinds = totalKinds)
        Spacer(Modifier.width(6.dp))
        RoundIconButton(
            icon = "ic_settings",
            contentDescription = "设置",
            onClick = onSettings,
            size = 36.dp,
            iconSize = 16.dp,
            tint = Color.White.copy(alpha = 0.7f),
            background = Color.White.copy(alpha = 0.05f),
            borderColor = Color.White.copy(alpha = 0.09f),
        )
        Spacer(Modifier.width(6.dp))
        RoundIconButton(
            icon = if (multiSelectEnabled) "ic_close" else "ic_multiselect",
            contentDescription = if (multiSelectEnabled) "退出多选" else "多选物品",
            onClick = onToggleMultiSelect,
            size = 36.dp,
            iconSize = 17.dp,
            tint = if (multiSelectEnabled) accent else Color.White.copy(alpha = 0.7f),
            background = if (multiSelectEnabled) accent.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.05f),
            borderColor = if (multiSelectEnabled) accent.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.09f),
        )
        Spacer(Modifier.width(8.dp))
        AddButton(accent = accent, onClick = onAdd)
    }
}

@Composable
private fun CountPill(totalQuantity: Int, totalKinds: Int) {
    GlassPanel(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(44.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlyphIcon(
                key = "ic_layers",
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.White.copy(alpha = 0.5f),
            )
            Spacer(Modifier.width(6.dp))
            AnimatedContent(
                targetState = totalQuantity,
                transitionSpec = {
                    (fadeIn(tween(160)) + slideInVertically { it / 2 })
                        .togetherWith(fadeOut(tween(120)))
                },
                label = "totalQuantity",
            ) { value ->
                Text(
                    text = value.toString(),
                    color = Mist,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.width(3.dp))
            Text(
                text = "件",
                color = Color.White.copy(alpha = 0.48f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun AddButton(accent: Color, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(accent.copy(alpha = 0.16f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(accent, accent.copy(alpha = 0.78f))))
                .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            GlyphIcon(
                key = "ic_add",
                contentDescription = "新建物品或分类",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF06231F),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryRail(
    categories: List<Category>,
    activeCategoryId: String,
    accent: Color,
    data: InventoryData,
    onCategorySelected: (String) -> Unit,
    onEditCategory: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    GlassPanel(
        modifier = modifier.width(68.dp),
        shape = RoundedCornerShape(27.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            categories.forEach { category ->
                val selected = category.id == activeCategoryId
                val tint by animateColorAsState(
                    if (selected) accent else Color.White.copy(alpha = 0.68f),
                    tween(220),
                    label = "railTint",
                )
                val bg by animateColorAsState(
                    if (selected) accent.copy(alpha = 0.14f) else Color.Transparent,
                    tween(220),
                    label = "railBg",
                )
                val border by animateColorAsState(
                    if (selected) accent.copy(alpha = 0.44f) else Color.Transparent,
                    tween(220),
                    label = "railBorder",
                )
                val interaction = remember { MutableInteractionSource() }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(bg)
                        .border(0.8.dp, border, RoundedCornerShape(18.dp))
                        .combinedClickable(
                            interactionSource = interaction,
                            indication = null,
                            onClick = { onCategorySelected(category.id) },
                            onLongClick = {
                                if (category.id != AllCategoryId) {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onEditCategory(category)
                                }
                            },
                            onLongClickLabel = if (category.id == AllCategoryId) null else "编辑分类",
                            role = androidx.compose.ui.semantics.Role.Button,
                        )
                        .padding(vertical = 7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier.size(36.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(accent.copy(alpha = 0.4f), Color.Transparent)
                                        ),
                                        CircleShape,
                                    )
                            )
                        }
                        CategoryGlyph(
                            category = category,
                            modifier = Modifier.size(22.dp),
                            tint = tint,
                        )
                    }
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = if (category.id == AllCategoryId) "全部" else category.name,
                        color = if (selected) Color.White.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.54f),
                        fontSize = 9.sp,
                        lineHeight = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(
    category: Category,
    items: List<InventoryItem>,
    accent: Color,
    isWide: Boolean,
    multiSelectEnabled: Boolean,
    onEditCategory: () -> Unit,
) {
    val quantity = items.sumOf { it.quantity }
    Column {
        SectionLabel(
            text = if (category.id == AllCategoryId) "COLLECTION / EVERYTHING" else "COLLECTION / CURATED",
            accent = accent.copy(alpha = 0.72f),
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Column(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = category,
                    transitionSpec = {
                        (fadeIn(tween(220)) + slideInVertically(tween(260)) { it / 3 })
                            .togetherWith(fadeOut(tween(140)))
                    },
                    label = "categoryTitle",
                ) { target ->
                    Text(
                        text = target.name,
                        color = Mist,
                        style = if (isWide) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${quantity} 件物品",
                        color = Color.White.copy(alpha = 0.52f),
                        fontSize = 11.sp,
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 7.dp)
                            .size(3.dp)
                            .background(Color.White.copy(alpha = 0.22f), CircleShape)
                    )
                    Text(
                        text = "${items.size} 种记录",
                        color = Color.White.copy(alpha = 0.36f),
                        fontSize = 11.sp,
                    )
                    if (items.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 7.dp)
                                .size(3.dp)
                                .background(Color.White.copy(alpha = 0.22f), CircleShape)
                        )
                        Text(
                            text = if (multiSelectEnabled) "长按多选" else "长按编辑",
                            color = accent.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
            if (category.id != AllCategoryId) {
                RoundIconButton(
                    icon = "ic_edit",
                    contentDescription = "编辑分类",
                    onClick = onEditCategory,
                    size = 38.dp,
                    iconSize = 17.dp,
                    tint = accent,
                    background = accent.copy(alpha = 0.09f),
                    borderColor = accent.copy(alpha = 0.2f),
                )
            }
        }
    }
}

@Composable
private fun MultiSelectBar(
    selectedCount: Int,
    totalCount: Int,
    accent: Color,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit,
    onDelete: () -> Unit,
    onExit: () -> Unit,
) {
    val allSelected = totalCount > 0 && selectedCount == totalCount
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f))
                    .border(0.8.dp, accent.copy(alpha = 0.32f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                GlyphIcon(
                    key = "ic_check",
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = accent,
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "已选 $selectedCount 件",
                color = Mist,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = if (allSelected) "全不选" else "全选",
                color = accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        if (allSelected) onClearSelection() else onSelectAll()
                    }
                    .padding(horizontal = 7.dp, vertical = 6.dp),
            )
            Spacer(Modifier.width(3.dp))
            RoundIconButton(
                icon = "ic_delete",
                contentDescription = "删除所选",
                onClick = {
                    if (selectedCount > 0) onDelete()
                },
                size = 31.dp,
                iconSize = 14.dp,
                tint = if (selectedCount > 0) Color(0xFFFF9B92) else Color.White.copy(alpha = 0.22f),
                background = if (selectedCount > 0) Color(0xFFFF6E66).copy(alpha = 0.08f) else Color.White.copy(alpha = 0.03f),
                borderColor = if (selectedCount > 0) Color(0xFFFF6E66).copy(alpha = 0.17f) else Color.White.copy(alpha = 0.06f),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "完成",
                color = Color.White.copy(alpha = 0.52f),
                fontSize = 11.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onExit)
                    .padding(horizontal = 7.dp, vertical = 6.dp),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InventoryItemCard(
    item: InventoryItem,
    selected: Boolean,
    multiSelectMode: Boolean,
    accent: Color,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val itemAccent = Color(item.accent)
    val shape = RoundedCornerShape(19.dp)
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(
                elevation = if (selected) 16.dp else 0.dp,
                shape = shape,
                ambientColor = accent.copy(alpha = 0.35f),
                spotColor = accent.copy(alpha = 0.45f),
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        itemAccent.copy(alpha = if (selected) 0.42f else 0.28f),
                        itemAccent.copy(alpha = 0.075f),
                        Color(0xFF0C1A20),
                    )
                )
            )
            .border(
                width = if (selected) 1.5.dp else 0.8.dp,
                brush = if (selected) {
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.95f), accent.copy(alpha = 0.8f))
                    )
                } else {
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.13f), Color.White.copy(alpha = 0.03f))
                    )
                },
                shape = shape,
            )
            .combinedClickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                },
                onLongClickLabel = "编辑物品",
                role = androidx.compose.ui.semantics.Role.Button,
            )
            .semantics {
                contentDescription = "${item.name}，数量 ${item.quantity}"
            },
    ) {
        if (item.customIconPath != null) {
            LocalImage(
                path = item.customIconPath,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.52f to Color(0xFF081318).copy(alpha = 0.05f),
                            1f to Color(0xFF061014).copy(alpha = 0.7f),
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.78f)
                    .background(
                        Brush.radialGradient(
                            listOf(itemAccent.copy(alpha = 0.38f), itemAccent.copy(alpha = 0.08f), Color.Transparent)
                        ),
                        CircleShape,
                    )
            )
            GlyphIcon(
                key = item.iconKey,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.48f),
                tint = lerp(Color.White, itemAccent, if (selected) 0.24f else 0.16f),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(9.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(itemAccent.copy(alpha = 0.85f))
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(34.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF030A0D).copy(alpha = 0.28f),
                            Color(0xFF030A0D).copy(alpha = 0.8f),
                        )
                    )
                )
        )
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 9.dp, bottom = 4.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = "×",
                color = Color.White.copy(alpha = 0.48f),
                fontSize = 9.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 2.dp),
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = item.quantity.toString(),
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }

        if (multiSelectMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(7.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (selected) accent else Color(0xFF061014).copy(alpha = 0.5f)
                    )
                    .border(
                        width = 1.dp,
                        color = if (selected) Color.White.copy(alpha = 0.82f) else Color.White.copy(alpha = 0.25f),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    GlyphIcon(
                        key = "ic_check",
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF06231F),
                    )
                }
            }
        }
    }
}

@Composable
fun ItemDetailPanel(
    item: InventoryItem,
    category: Category,
    index: Int,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    onEdit: () -> Unit,
) {
    GlassPanel(
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
    ) {
        ItemDetailBody(
            item = item,
            category = category,
            index = index,
            onEdit = onEdit,
            modifier = Modifier.fillMaxSize(),
            compact = compact,
        )
    }
}

@Composable
fun ItemDetailBody(
    item: InventoryItem,
    category: Category,
    index: Int,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    headerExtra: (@Composable () -> Unit)? = null,
) {
    val accent = Color(item.accent)
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(if (compact) 16.dp else 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("ITEM DOSSIER", accent = accent.copy(alpha = 0.72f))
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "物品详情",
                    color = Mist,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Text(
                text = "No. ${index.toString().padStart(3, '0')}",
                color = Color.White.copy(alpha = 0.28f),
                fontSize = 10.sp,
                letterSpacing = 1.sp,
            )
            if (headerExtra != null) {
                Spacer(Modifier.width(6.dp))
                headerExtra()
            }
        }
        Spacer(Modifier.height(if (compact) 10.dp else 16.dp))
        DetailHero(item = item, category = category, accent = accent, compact = compact)
        Spacer(Modifier.height(if (compact) 10.dp else 16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(accent.copy(alpha = 0.11f))
                    .border(0.7.dp, accent.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = category.name,
                    color = accent.copy(alpha = 0.92f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "共 ${item.quantity} 件",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
            )
        }
        Spacer(Modifier.height(if (compact) 7.dp else 12.dp))
        Text(
            text = item.name,
            color = Mist,
            style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(if (compact) 10.dp else 14.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.075f))
        Spacer(Modifier.height(13.dp))
        SectionLabel("NOTES / 关于它", accent = Color.White.copy(alpha = 0.3f))
        Spacer(Modifier.height(7.dp))
        Text(
            text = item.note.ifBlank { "还没有写下说明。长按物品，为它留下一点文字。" },
            color = Color.White.copy(alpha = if (item.note.isBlank()) 0.32f else 0.67f),
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
        )
        Spacer(Modifier.height(16.dp))
        if (item.location.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White.copy(alpha = 0.038f))
                    .border(0.7.dp, Color.White.copy(alpha = 0.065f), RoundedCornerShape(15.dp))
                    .padding(horizontal = 12.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(27.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    GlyphIcon(
                        key = "ic_map_pin",
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = accent.copy(alpha = 0.9f),
                    )
                }
                Spacer(Modifier.width(9.dp))
                Column {
                    Text(
                        text = "存放位置",
                        color = Color.White.copy(alpha = 0.31f),
                        fontSize = 8.sp,
                        letterSpacing = 0.8.sp,
                    )
                    Text(
                        text = item.location,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DetailMeta(
                label = "收入于",
                value = item.createdDateLabel(),
                modifier = Modifier.weight(1f),
            )
            DetailMeta(
                label = "分类",
                value = category.name,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(18.dp))
        AccentButton(
            text = "编辑这件物品",
            accent = accent,
            icon = "ic_edit",
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DetailHero(
    item: InventoryItem,
    category: Category,
    accent: Color,
    compact: Boolean = false,
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compact) 128.dp else 184.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        accent.copy(alpha = 0.54f),
                        accent.copy(alpha = 0.17f),
                        Color(0xFF0A171D),
                    )
                )
            )
            .border(0.9.dp, Color.White.copy(alpha = 0.12f), shape),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = if (compact) 14.dp else 22.dp, end = if (compact) 14.dp else 18.dp)
                .size(if (compact) 62.dp else 84.dp)
                .background(
                    Brush.radialGradient(
                        listOf(Color.White.copy(alpha = 0.13f), Color.Transparent)
                    ),
                    CircleShape,
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = if (compact) 22.dp else 28.dp)
                .size(if (compact) 68.dp else 92.dp)
                .border(1.dp, Color.White.copy(alpha = 0.11f), CircleShape)
        )
        if (item.customIconPath != null) {
            LocalImage(
                path = item.customIconPath,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = if (compact) 30.dp else 42.dp)
                    .size(if (compact) 60.dp else 84.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(20.dp)),
            )
        } else {
            GlyphIcon(
                key = item.iconKey,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = if (compact) 30.dp else 42.dp)
                    .size(if (compact) 52.dp else 72.dp),
                tint = Color.White.copy(alpha = 0.9f),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(if (compact) 14.dp else 18.dp),
        ) {
            Text(
                text = category.name,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 9.sp,
                letterSpacing = 1.4.sp,
            )
            Spacer(Modifier.height(if (compact) 2.dp else 4.dp))
            Text(
                text = "× ${item.quantity}",
                color = Color.White,
                fontSize = if (compact) 18.sp else 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (!compact) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "COLLECTED OBJECT",
                    color = Color.White.copy(alpha = 0.37f),
                    fontSize = 8.sp,
                    letterSpacing = 1.2.sp,
                )
            }
        }
    }
}

@Composable
private fun DetailMeta(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.032f))
            .border(0.7.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 8.sp,
            letterSpacing = 0.7.sp,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
