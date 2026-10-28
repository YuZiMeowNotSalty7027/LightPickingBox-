package com.miraitowa.shiguang.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraitowa.shiguang.data.AccentPalette
import com.miraitowa.shiguang.ui.components.AccentButton
import com.miraitowa.shiguang.ui.components.ConfigureSheetSystemBars
import com.miraitowa.shiguang.ui.components.GlyphIcon
import com.miraitowa.shiguang.ui.components.GlassPanel
import com.miraitowa.shiguang.ui.components.RoundIconButton
import com.miraitowa.shiguang.ui.components.SectionLabel
import com.miraitowa.shiguang.ui.theme.Mist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    currentAccent: Long?,
    fallbackAccent: Long,
    appTitle: String,
    onDismiss: () -> Unit,
    onSelect: (Long?) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var chosen by remember { mutableStateOf(currentAccent) }
    val previewColor by animateColorAsState(
        targetValue = chosen?.let { Color(it) } ?: Color(fallbackAccent),
        animationSpec = tween(350),
        label = "themePreview",
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color(0xFF02090C).copy(alpha = 0.64f),
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
                        listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.03f))
                    ),
                    RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
                ),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp),
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
                        SectionLabel("SETTINGS", accent = previewColor.copy(alpha = 0.75f))
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = "设置",
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
                    text = "外观、文字与这只匣子的小习惯。",
                    color = Color.White.copy(alpha = 0.36f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 22.dp),
                )
                Spacer(Modifier.height(18.dp))
                SectionLabel(
                    "GLOBAL COLOR / 全局颜色",
                    accent = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.padding(horizontal = 22.dp),
                )
                Spacer(Modifier.height(9.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(previewColor.copy(alpha = 0.95f), previewColor.copy(alpha = 0.5f))
                                )
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        GlyphIcon(
                            key = "ic_package",
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color(0xFF06231F),
                        )
                    }
                    Spacer(Modifier.width(13.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = appTitle,
                            color = Mist,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = if (chosen == null) "跟随当前分类的颜色" else "统一主题色 · 即时生效",
                            color = Color.White.copy(alpha = 0.38f),
                            fontSize = 10.sp,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(previewColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        GlyphIcon(
                            key = "ic_add",
                            contentDescription = null,
                            modifier = Modifier.size(19.dp),
                            tint = Color(0xFF06231F),
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                ThemeModeCard(
                    selected = chosen == null,
                    onClick = {
                        chosen = null
                        onSelect(null)
                    },
                    modifier = Modifier.padding(horizontal = 18.dp),
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionLabel("UNIFIED COLOR", accent = Color.White.copy(alpha = 0.3f))
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "点按即应用",
                        color = Color.White.copy(alpha = 0.24f),
                        fontSize = 9.sp,
                    )
                }
                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AccentPalette.chunked(5).forEach { rowColors ->
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            rowColors.forEach { colorValue ->
                                ThemeSwatch(
                                    color = Color(colorValue),
                                    selected = chosen == colorValue,
                                    onClick = {
                                        chosen = colorValue
                                        onSelect(colorValue)
                                    },
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GlyphIcon(
                        key = "ic_edit",
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color.White.copy(alpha = 0.28f),
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = "长按左上角标题，可自定义主标题与副标题",
                        color = Color.White.copy(alpha = 0.3f),
                        fontSize = 9.sp,
                    )
                }
                Spacer(Modifier.height(7.dp))
                Text(
                    text = "拾光匣 1.0.2 · 所有数据仅保存在本机",
                    color = Color.White.copy(alpha = 0.2f),
                    fontSize = 9.sp,
                    modifier = Modifier.padding(horizontal = 22.dp),
                )
                Spacer(Modifier.height(24.dp))
                AccentButton(
                    text = "完成",
                    accent = previewColor,
                    icon = "ic_check",
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                )
            }
        }
    }
}

@Composable
private fun ThemeModeCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    GlassPanel(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        borderAlpha = if (selected) 1.4f else 1f,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(Modifier.size(27.dp)) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(AccentPalette[0]).copy(alpha = 0.9f))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(AccentPalette[4]).copy(alpha = 0.92f))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(AccentPalette[2]).copy(alpha = 0.88f))
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "跟随分类",
                    color = Mist,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "每个分类保留自己的强调色",
                    color = Color.White.copy(alpha = 0.36f),
                    fontSize = 10.sp,
                )
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (selected) Color(0xFF6FE7D2) else Color.White.copy(alpha = 0.05f))
                    .border(
                        1.dp,
                        if (selected) Color.White.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.1f),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    GlyphIcon(
                        key = "ic_check",
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color(0xFF06231F),
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeSwatch(
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color.White.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.12f),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            GlyphIcon(
                key = "ic_check",
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                tint = Color(0xFF061613),
            )
        }
    }
}
