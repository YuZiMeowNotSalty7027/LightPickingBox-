package com.miraitowa.shiguang.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraitowa.shiguang.data.AccentPalette
import com.miraitowa.shiguang.data.Category
import com.miraitowa.shiguang.data.CategoryIconOptions
import com.miraitowa.shiguang.data.newCategoryId
import com.miraitowa.shiguang.ui.components.AccentButton
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
fun CategoryEditorSheet(
    category: Category?,
    itemCount: Int,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit,
    onDelete: ((Category) -> Unit)? = null,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isEditing = category != null

    var name by remember(category?.id) { mutableStateOf(category?.name ?: "") }
    var iconKey by remember(category?.id) { mutableStateOf(category?.iconKey ?: "ic_boxes") }
    var customIconPath by remember(category?.id) { mutableStateOf(category?.customIconPath) }
    var accentValue by remember(category?.id) { mutableStateOf(category?.accent ?: AccentPalette.first()) }
    var nameError by remember { mutableStateOf(false) }
    var deleteConfirm by remember { mutableStateOf(false) }
    var pickingImage by remember { mutableStateOf(false) }
    var imageError by remember { mutableStateOf(false) }
    val accent = Color(accentValue)

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            pickingImage = true
            scope.launch {
                val path = withContext(Dispatchers.IO) { ImageStorage.importFromUri(context, uri) }
                imageError = path == null
                if (path != null) customIconPath = path
                pickingImage = false
            }
        }
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
                .fillMaxHeight(0.84f)
                .clip(RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF14272E),
                            Color(0xFF0C1B21),
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
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = 10.dp)
                    .size(170.dp)
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
                            text = if (isEditing) "EDIT COLLECTION" else "NEW COLLECTION",
                            accent = accent.copy(alpha = 0.7f),
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = if (isEditing) "编辑分类" else "新建分类",
                            color = Mist,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    if (isEditing) {
                        RoundIconButton(
                            icon = "ic_delete",
                            contentDescription = "删除分类",
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
                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(78.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(accent.copy(alpha = 0.36f), Color(0xFF102128))
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(22.dp))
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (customIconPath != null) {
                            LocalImage(path = customIconPath!!, modifier = Modifier.fillMaxSize())
                        } else {
                            GlyphIcon(
                                key = iconKey,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color.White.copy(alpha = 0.9f),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF071318).copy(alpha = 0.62f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            GlyphIcon(
                                key = "ic_camera",
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = Color.White.copy(alpha = 0.7f),
                            )
                        }
                    }
                    Spacer(Modifier.width(15.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name.ifBlank { "新的分类" },
                            color = if (name.isBlank()) Color.White.copy(alpha = 0.37f) else Mist,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = if (pickingImage) "正在整理图片…" else if (isEditing) "这个分类里有 $itemCount 件物品" else "为生活的一角，取一个名字",
                            color = Color.White.copy(alpha = 0.38f),
                            fontSize = 10.sp,
                        )
                        Spacer(Modifier.height(7.dp))
                        Text(
                            text = if (imageError) "这张图片没有读取成功，请换一张试试" else "轻点图标可从相册上传",
                            color = if (imageError) Color(0xFFFF9B92) else accent.copy(alpha = 0.62f),
                            fontSize = 9.sp,
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))
                EditorSection(title = "分类名称", english = "NAME") {
                    FormTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        placeholder = "例如：露营装备",
                        accent = accent,
                        singleLine = true,
                        isError = nameError,
                    )
                    if (nameError) {
                        Text(
                            text = "分类也需要一个名字。",
                            color = Color(0xFFFF9B92),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(start = 3.dp, top = 5.dp),
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
                EditorSection(
                    title = "分类图标",
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
                        items(CategoryIconOptions, key = { it.key + it.label }) { option ->
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
                        GhostButton(
                            text = "改用预设图标",
                            onClick = { customIconPath = null },
                            contentColor = Color.White.copy(alpha = 0.48f),
                            modifier = Modifier.height(34.dp),
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
                EditorSection(title = "分类色调", english = "ACCENT") {
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

                Spacer(Modifier.height(26.dp))
                AccentButton(
                    text = if (isEditing) "保存分类" else "创建分类",
                    accent = accent,
                    icon = "ic_check",
                    onClick = save@{
                        if (name.isBlank()) {
                            nameError = true
                            return@save
                        }
                        val now = System.currentTimeMillis()
                        val saved = category?.copy(
                            name = name.trim(),
                            iconKey = iconKey,
                            customIconPath = customIconPath,
                            accent = accentValue,
                        ) ?: Category(
                            id = newCategoryId(),
                            name = name.trim(),
                            iconKey = iconKey,
                            customIconPath = customIconPath,
                            accent = accentValue,
                            order = now.toInt(),
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

    if (deleteConfirm && category != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirm = false },
            title = {
                Text("删除这个分类？", color = Mist, fontWeight = FontWeight.SemiBold)
            },
            text = {
                Text(
                    if (itemCount > 0) {
                        "「${category.name}」中的 $itemCount 件物品会移动到「其他」分类，物品本身不会被删除。"
                    } else {
                        "「${category.name}」将被移除，分类里的物品会移动到「其他」。"
                    },
                    color = MutedText,
                    fontSize = 13.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    deleteConfirm = false
                    onDelete?.invoke(category)
                }) {
                    Text("删除", color = Color(0xFFFF9B92))
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
