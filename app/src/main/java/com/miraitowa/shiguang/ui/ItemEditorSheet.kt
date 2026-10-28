package com.miraitowa.shiguang.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraitowa.shiguang.data.AccentPalette
import com.miraitowa.shiguang.data.Category
import com.miraitowa.shiguang.data.IconOption
import com.miraitowa.shiguang.data.InventoryItem
import com.miraitowa.shiguang.data.ItemIconOptions
import com.miraitowa.shiguang.data.newItemId
import com.miraitowa.shiguang.ui.components.AccentButton
import com.miraitowa.shiguang.ui.components.CategoryGlyph
import com.miraitowa.shiguang.ui.components.ConfigureSheetSystemBars
import com.miraitowa.shiguang.ui.components.CustomIconChoice
import com.miraitowa.shiguang.ui.components.GhostButton
import com.miraitowa.shiguang.ui.components.GlyphIcon
import com.miraitowa.shiguang.ui.components.IconChoice
import com.miraitowa.shiguang.ui.components.LocalImage
import com.miraitowa.shiguang.ui.components.RoundIconButton
import com.miraitowa.shiguang.ui.components.SectionLabel
import com.miraitowa.shiguang.ui.theme.Mist
import com.miraitowa.shiguang.ui.theme.MutedText
import com.miraitowa.shiguang.util.ImageStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemEditorSheet(
    item: InventoryItem?,
    categories: List<Category>,
    defaultCategoryId: String,
    onDismiss: () -> Unit,
    onSave: (InventoryItem) -> Unit,
    onDelete: ((InventoryItem) -> Unit)? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEditing = item != null
    val fallbackCategory = categories.firstOrNull { it.id == defaultCategoryId } ?: categories.firstOrNull()
    val initialCategory = item?.categoryId ?: fallbackCategory?.id ?: defaultCategoryId

    var name by remember(item?.id) { mutableStateOf(item?.name ?: "") }
    var note by remember(item?.id) { mutableStateOf(item?.note ?: "") }
    var location by remember(item?.id) { mutableStateOf(item?.location ?: "") }
    var quantityText by remember(item?.id) { mutableStateOf((item?.quantity ?: 1).toString()) }
    var categoryId by remember(item?.id) { mutableStateOf(initialCategory) }
    var iconKey by remember(item?.id) { mutableStateOf(item?.iconKey ?: "ic_sparkles") }
    var customIconPath by remember(item?.id) { mutableStateOf(item?.customIconPath) }
    var accentValue by remember(item?.id) {
        mutableStateOf(item?.accent ?: (categories.firstOrNull { it.id == initialCategory }?.accent ?: AccentPalette.first()))
    }
    var nameError by remember { mutableStateOf(false) }
    var deleteConfirm by remember { mutableStateOf(false) }
    var pickingImage by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }
    val accent = Color(accentValue)
    val selectedCategory = categories.firstOrNull { it.id == categoryId } ?: fallbackCategory ?: categories.first()

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            pickingImage = true
            scope.launch {
                val path = withContext(Dispatchers.IO) { ImageStorage.importFromUri(context, uri) }
                imageError = path == null
                if (path != null) {
                    customIconPath = path
                }
                pickingImage = false
            }
        }
    }

    fun increase() {
        val value = quantityText.toIntOrNull() ?: 1
        quantityText = (value + 1).coerceAtMost(9999).toString()
    }

    fun decrease() {
        val value = quantityText.toIntOrNull() ?: 1
        quantityText = (value - 1).coerceAtLeast(0).toString()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color(0xFF02090C).copy(alpha = 0.66f),
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        modifier = Modifier.imePadding(),
    ) {
        ConfigureSheetSystemBars()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f)
                .clip(RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF13262D),
                            Color(0xFF0B1A20),
                            Color(0xFF071318),
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.03f))
                    ),
                    shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
                ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 28.dp, top = 0.dp)
                    .size(160.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(accent.copy(alpha = 0.14f), Color.Transparent)
                        ),
                        CircleShape,
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(bottom = 22.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 22.dp, end = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .width(38.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.18f))
                    )
                    Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        SectionLabel(
                            text = if (isEditing) "EDIT OBJECT" else "NEW OBJECT",
                            accent = accent.copy(alpha = 0.7f),
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = if (isEditing) "编辑物品" else "新建物品",
                            color = Mist,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    if (isEditing) {
                        RoundIconButton(
                            icon = "ic_delete",
                            contentDescription = "删除物品",
                            onClick = { deleteConfirm = true },
                            size = 40.dp,
                            tint = Color(0xFFFF9B92),
                            background = Color(0xFFFF6E66).copy(alpha = 0.08f),
                            borderColor = Color(0xFFFF6E66).copy(alpha = 0.16f),
                            iconSize = 17.dp,
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    RoundIconButton(
                        icon = "ic_close",
                        contentDescription = "关闭",
                        onClick = onDismiss,
                        size = 40.dp,
                        background = Color.White.copy(alpha = 0.06f),
                        borderColor = Color.White.copy(alpha = 0.1f),
                        iconSize = 17.dp,
                    )
                }
                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PreviewTile(
                        iconKey = iconKey,
                        customIconPath = customIconPath,
                        accent = accent,
                        onClick = { imagePicker.launch("image/*") },
                    )
                    Spacer(Modifier.width(15.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name.ifBlank { "新的物品" },
                            color = if (name.isBlank()) Color.White.copy(alpha = 0.37f) else Mist,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(Modifier.height(5.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlyphIcon(
                                key = selectedCategory.iconKey,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = accent.copy(alpha = 0.8f),
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = selectedCategory.name,
                                color = Color.White.copy(alpha = 0.42f),
                                fontSize = 10.sp,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "数量 ${quantityText.ifBlank { "0" }}",
                                color = Color.White.copy(alpha = 0.32f),
                                fontSize = 10.sp,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = when {
                                pickingImage -> "正在整理图片…"
                                imageError -> "这张图片没有读取成功，请换一张试试"
                                else -> "轻点左侧图标，从相册更换物品图片"
                            },
                            color = if (imageError) Color(0xFFFF9B92) else accent.copy(alpha = 0.62f),
                            fontSize = 9.sp,
                        )
                    }
                }
                Spacer(Modifier.height(22.dp))

                EditorSection(title = "名称", english = "NAME") {
                    FormTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        placeholder = "例如：通勤降噪耳机",
                        accent = accent,
                        singleLine = true,
                        isError = nameError,
                    )
                    if (nameError) {
                        Text(
                            text = "先给它一个名字，再收入匣中。",
                            color = Color(0xFFFF9B92),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 3.dp, top = 5.dp),
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
                EditorSection(title = "数量", english = "QUANTITY") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StepButton(icon = "ic_minus", accent = accent, onClick = ::decrease)
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .width(98.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(Color.White.copy(alpha = 0.045f))
                                .border(0.8.dp, Color.White.copy(alpha = 0.09f), RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicTextField(
                                value = quantityText,
                                onValueChange = { raw ->
                                    val digits = raw.filter { it.isDigit() }.take(4)
                                    quantityText = digits
                                },
                                textStyle = TextStyle(
                                    color = Mist,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                cursorBrush = SolidColor(accent),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        StepButton(icon = "ic_plus", accent = accent, onClick = ::increase)
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "长按物品可随时修改",
                            color = Color.White.copy(alpha = 0.25f),
                            fontSize = 9.sp,
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
                EditorSection(title = "分类", english = "CATEGORY") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 1.dp),
                    ) {
                        items(categories, key = { it.id }) { category ->
                            CategoryChip(
                                category = category,
                                selected = category.id == categoryId,
                                accent = Color(category.accent),
                                onClick = {
                                    categoryId = category.id
                                    if (!isEditing && customIconPath == null) {
                                        accentValue = category.accent
                                    }
                                },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                EditorSection(
                    title = "图标",
                    english = "GLYPH / IMAGE",
                    action = {
                        GhostButton(
                            text = if (customIconPath == null) "从相册上传" else "更换图片",
                            icon = "ic_image_add",
                            onClick = { imagePicker.launch("image/*") },
                            contentColor = accent,
                            modifier = Modifier.height(38.dp),
                        )
                    },
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 1.dp),
                    ) {
                        item {
                            CustomIconChoice(
                                selected = customIconPath != null,
                                customIconPath = customIconPath,
                                accent = accent,
                                onClick = { imagePicker.launch("image/*") },
                            )
                        }
                        items(ItemIconOptions, key = { it.key + it.label }) { option ->
                            IconChoice(
                                option = option,
                                selected = customIconPath == null && iconKey == option.key,
                                accent = accent,
                                onClick = {
                                    iconKey = option.key
                                    customIconPath = null
                                },
                            )
                        }
                    }
                    if (customIconPath != null) {
                        Spacer(Modifier.height(9.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(11.dp))
                            ) {
                                LocalImage(path = customIconPath!!, modifier = Modifier.fillMaxSize())
                            }
                            Spacer(Modifier.width(9.dp))
                            Text(
                                text = "正在使用自定义图片",
                                color = Color.White.copy(alpha = 0.48f),
                                fontSize = 10.sp,
                            )
                            Spacer(Modifier.weight(1f))
                            GhostButton(
                                text = "使用预设图标",
                                onClick = { customIconPath = null },
                                contentColor = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.height(34.dp),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                EditorSection(title = "色调", english = "ACCENT") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 1.dp),
                    ) {
                        items(AccentPalette) { value ->
                            AccentSwatch(
                                color = Color(value),
                                selected = accentValue == value,
                                onClick = { accentValue = value },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                EditorSection(title = "说明", english = "NOTES") {
                    FormTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = "写下关于它的故事、使用习惯，或下次记得的事…",
                        accent = accent,
                        singleLine = false,
                        minHeight = 112.dp,
                    )
                }

                Spacer(Modifier.height(18.dp))
                EditorSection(title = "存放位置", english = "PLACE") {
                    FormTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = "例如：书桌第二层 / 通勤包前袋",
                        accent = accent,
                        singleLine = true,
                    )
                }

                Spacer(Modifier.height(26.dp))
                AccentButton(
                    text = if (isEditing) "保存修改" else "收入拾光匣",
                    accent = accent,
                    icon = if (isEditing) "ic_check" else "ic_add",
                    onClick = save@{
                        if (name.isBlank()) {
                            nameError = true
                            return@save
                        }
                        val quantity = (quantityText.toIntOrNull() ?: 0).coerceIn(0, 9999)
                        val now = System.currentTimeMillis()
                        val saved = item?.copy(
                            name = name.trim(),
                            note = note.trim(),
                            location = location.trim(),
                            quantity = quantity,
                            categoryId = categoryId,
                            iconKey = iconKey,
                            customIconPath = customIconPath,
                            accent = accentValue,
                            updatedAt = now,
                        ) ?: InventoryItem(
                            id = newItemId(),
                            categoryId = categoryId,
                            name = name.trim(),
                            note = note.trim(),
                            location = location.trim(),
                            quantity = quantity,
                            iconKey = iconKey,
                            customIconPath = customIconPath,
                            accent = accentValue,
                            createdAt = now,
                            updatedAt = now,
                        )
                        onSave(saved)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                )
            }
        }
    }

    if (deleteConfirm && item != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirm = false },
            title = {
                Text("移除这件物品？", color = Mist, fontWeight = FontWeight.SemiBold)
            },
            text = {
                Text(
                    "「${item.name}」将从拾光匣中移除，这个操作不会影响现实中的它。",
                    color = MutedText,
                    fontSize = 13.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    deleteConfirm = false
                    onDelete?.invoke(item)
                }) {
                    Text("移除", color = Color(0xFFFF9B92))
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirm = false }) {
                    Text("保留", color = Color.White.copy(alpha = 0.65f))
                }
            },
            containerColor = Color(0xFF102027),
            shape = RoundedCornerShape(26.dp),
        )
    }
}

@Composable
internal fun EditorSection(
    title: String,
    english: String,
    action: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 22.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = english,
                color = Color.White.copy(alpha = 0.22f),
                fontSize = 8.sp,
                letterSpacing = 0.8.sp,
            )
            Spacer(Modifier.weight(1f))
            action?.invoke()
        }
        Spacer(Modifier.height(9.dp))
        content()
    }
}

@Composable
private fun PreviewTile(
    iconKey: String,
    customIconPath: String?,
    accent: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(78.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(accent.copy(alpha = 0.34f), Color(0xFF102128))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (customIconPath != null) {
            LocalImage(
                path = customIconPath,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF071318).copy(alpha = 0.78f)),
                contentAlignment = Alignment.Center,
            ) {
                GlyphIcon(
                    key = "ic_camera",
                    contentDescription = null,
                    modifier = Modifier.size(11.dp),
                    tint = Color.White.copy(alpha = 0.8f),
                )
            }
        } else {
            GlyphIcon(
                key = iconKey,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White.copy(alpha = 0.9f),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF071318).copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center,
            ) {
                GlyphIcon(
                    key = "ic_camera",
                    contentDescription = null,
                    modifier = Modifier.size(11.dp),
                    tint = Color.White.copy(alpha = 0.65f),
                )
            }
        }
    }
}

@Composable
internal fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    accent: Color,
    singleLine: Boolean,
    modifier: Modifier = Modifier,
    minHeight: androidx.compose.ui.unit.Dp = 48.dp,
    isError: Boolean = false,
) {
    val shape = RoundedCornerShape(15.dp)
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            color = Mist,
            fontSize = 14.sp,
            lineHeight = 22.sp,
        ),
        cursorBrush = SolidColor(accent),
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 5,
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minHeight)
                    .clip(shape)
                    .background(Color.White.copy(alpha = 0.042f))
                    .border(
                        width = if (isError) 1.dp else 0.8.dp,
                        color = if (isError) Color(0xFFFF6E66).copy(alpha = 0.7f) else Color.White.copy(alpha = 0.085f),
                        shape = shape,
                    )
                    .padding(horizontal = 14.dp, vertical = 13.dp),
                contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.White.copy(alpha = 0.27f),
                        fontSize = 12.sp,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun StepButton(
    icon: String,
    accent: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(accent.copy(alpha = 0.1f))
            .border(0.7.dp, accent.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        GlyphIcon(
            key = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = accent,
        )
    }
}

@Composable
private fun CategoryChip(
    category: Category,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = Modifier
            .height(42.dp)
            .clip(shape)
            .background(if (selected) accent.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f))
            .border(
                width = if (selected) 1.dp else 0.8.dp,
                color = if (selected) accent.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CategoryGlyph(
            category = category,
            modifier = Modifier.size(15.dp),
            tint = if (selected) accent else Color.White.copy(alpha = 0.55f),
        )
        Spacer(Modifier.width(7.dp))
        Text(
            text = category.name,
            color = if (selected) Mist else Color.White.copy(alpha = 0.55f),
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
        )
    }
}

@Composable
internal fun AccentSwatch(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.92f))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.12f),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            GlyphIcon(
                key = "ic_check",
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF061613),
            )
        }
    }
}
