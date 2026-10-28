package com.miraitowa.shiguang.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.miraitowa.shiguang.ui.components.AccentButton
import com.miraitowa.shiguang.ui.components.ConfigureSheetSystemBars
import com.miraitowa.shiguang.ui.components.GhostButton
import com.miraitowa.shiguang.ui.components.GlassPanel
import com.miraitowa.shiguang.ui.components.GlyphIcon
import com.miraitowa.shiguang.ui.components.RoundIconButton
import com.miraitowa.shiguang.ui.components.SectionLabel
import com.miraitowa.shiguang.ui.theme.Mist

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTitleEditorSheet(
    currentTitle: String,
    currentSubtitle: String,
    accent: Color,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf(currentTitle) }
    var subtitle by remember { mutableStateOf(currentSubtitle) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color(0xFF02090C).copy(alpha = 0.66f),
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
                        SectionLabel("CUSTOM TITLE", accent = accent.copy(alpha = 0.72f))
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = "自定义左上角文字",
                            color = Mist,
                            fontSize = 23.sp,
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
                    text = "主标题适合短句，副标题可以是一句小字。",
                    color = Color.White.copy(alpha = 0.36f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 22.dp),
                )
                Spacer(Modifier.height(18.dp))

                GlassPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    shape = RoundedCornerShape(22.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                    ) {
                        Text(
                            text = title.ifBlank { "拾光匣" },
                            color = Mist,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.6.sp,
                            maxLines = 1,
                        )
                        if (subtitle.isNotBlank()) {
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = subtitle,
                                color = accent.copy(alpha = 0.72f),
                                fontSize = 9.sp,
                                letterSpacing = 1.1.sp,
                                maxLines = 1,
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .width(38.dp)
                                .height(2.dp)
                                .clip(CircleShape)
                                .background(accent.copy(alpha = 0.7f))
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                EditorSection(title = "主标题", english = "TITLE") {
                    FormTextField(
                        value = title,
                        onValueChange = { title = it.take(14) },
                        placeholder = "拾光匣",
                        accent = accent,
                        singleLine = true,
                    )
                }
                Spacer(Modifier.height(16.dp))
                EditorSection(title = "副标题", english = "SUBTITLE") {
                    FormTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it.take(30) },
                        placeholder = "留白，或写一句喜欢的话",
                        accent = accent,
                        singleLine = true,
                    )
                }
                Spacer(Modifier.height(22.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GhostButton(
                        text = "恢复默认",
                        icon = "ic_rotate",
                        onClick = {
                            title = "拾光匣"
                            subtitle = "A LITTLE LIFE ARCHIVE"
                        },
                        contentColor = Color.White.copy(alpha = 0.55f),
                        modifier = Modifier.height(48.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    AccentButton(
                        text = "保存文字",
                        accent = accent,
                        icon = "ic_check",
                        onClick = {
                            onSave(
                                title.trim().ifBlank { "拾光匣" },
                                subtitle.trim(),
                            )
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    GlyphIcon(
                        key = "ic_info",
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color.White.copy(alpha = 0.25f),
                    )
                    Spacer(Modifier.width(7.dp))
                    Text(
                        text = "保存后，长按主界面左上角文字可以再次修改。",
                        color = Color.White.copy(alpha = 0.28f),
                        fontSize = 9.sp,
                    )
                }
            }
        }
    }
}
