package com.miraitowa.shiguang.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraitowa.shiguang.data.Category
import com.miraitowa.shiguang.data.InventoryItem
import com.miraitowa.shiguang.ui.components.ConfigureSheetSystemBars
import com.miraitowa.shiguang.ui.components.GlassPanel
import com.miraitowa.shiguang.ui.components.GlyphIcon
import com.miraitowa.shiguang.ui.components.RoundIconButton
import com.miraitowa.shiguang.ui.components.SectionLabel
import com.miraitowa.shiguang.ui.theme.Mist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMenuSheet(
    accent: Color,
    onDismiss: () -> Unit,
    onNewItem: () -> Unit,
    onNewCategory: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color(0xFF02090C).copy(alpha = 0.6f),
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    ) {
        ConfigureSheetSystemBars()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF14272E), Color(0xFF0A191F), Color(0xFF071318))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.17f), Color.White.copy(alpha = 0.03f))
                    ),
                    RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
                ),
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = 18.dp),
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
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        SectionLabel("ADD TO ARCHIVE", accent = accent.copy(alpha = 0.7f))
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = "添加到拾光匣",
                            color = Mist,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
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
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "记录一件真实物品，或重新整理生活的一角。",
                    color = Color.White.copy(alpha = 0.35f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 22.dp),
                )
                Spacer(Modifier.height(18.dp))
                CreateOption(
                    icon = "ic_package",
                    title = "新建物品",
                    subtitle = "记下名称、数量、图片与存放位置",
                    accent = accent,
                    onClick = onNewItem,
                    modifier = Modifier.padding(horizontal = 18.dp),
                )
                Spacer(Modifier.height(10.dp))
                CreateOption(
                    icon = "ic_folder",
                    title = "新建分类",
                    subtitle = "为物品建立新的收纳方式，可上传图标",
                    accent = accent,
                    onClick = onNewCategory,
                    modifier = Modifier.padding(horizontal = 18.dp),
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GlyphIcon(
                        key = "ic_sparkles",
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color.White.copy(alpha = 0.28f),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "小提示：长按任意物品，可以直接修改数量与资料",
                        color = Color.White.copy(alpha = 0.27f),
                        fontSize = 9.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateOption(
    icon: String,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassPanel(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(23.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(accent.copy(alpha = 0.28f), accent.copy(alpha = 0.07f))
                        )
                    )
                    .border(0.8.dp, accent.copy(alpha = 0.26f), RoundedCornerShape(17.dp)),
                contentAlignment = Alignment.Center,
            ) {
                GlyphIcon(
                    key = icon,
                    contentDescription = null,
                    modifier = Modifier.size(23.dp),
                    tint = accent,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Mist,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.36f),
                    fontSize = 10.sp,
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.045f)),
                contentAlignment = Alignment.Center,
            ) {
                GlyphIcon(
                    key = "ic_arrow_up_right",
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White.copy(alpha = 0.4f),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailSheet(
    item: InventoryItem,
    category: Category,
    index: Int,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color(0xFF02090C).copy(alpha = 0.62f),
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
    ) {
        ConfigureSheetSystemBars()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF13262D), Color(0xFF0A171D), Color(0xFF071217))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.03f))
                    ),
                    RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
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
                ItemDetailBody(
                    item = item,
                    category = category,
                    index = index,
                    onEdit = onEdit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    headerExtra = {
                        RoundIconButton(
                            icon = "ic_close",
                            contentDescription = "关闭详情",
                            onClick = onDismiss,
                            size = 36.dp,
                            background = Color.White.copy(alpha = 0.055f),
                            borderColor = Color.White.copy(alpha = 0.09f),
                            iconSize = 15.dp,
                        )
                    },
                )
            }
        }
    }
}
