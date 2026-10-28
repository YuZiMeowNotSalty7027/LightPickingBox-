package com.miraitowa.shiguang.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion.Crop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miraitowa.shiguang.R
import com.miraitowa.shiguang.data.Category
import com.miraitowa.shiguang.data.IconOption
import com.miraitowa.shiguang.ui.theme.Mist
import com.miraitowa.shiguang.util.ImageStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@Composable
fun AuroraBackdrop(
    accent: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF061015),
                        Color(0xFF0A171C),
                        Color(0xFF071116),
                    )
                )
            )
            .drawWithCache {
                val width = size.width
                val height = size.height
                val glow = Brush.radialGradient(
                    colors = listOf(accent.copy(alpha = 0.18f), accent.copy(alpha = 0.055f), Color.Transparent),
                    center = Offset(width * 0.16f, height * 0.08f),
                    radius = width * 1.05f,
                )
                val blueGlow = Brush.radialGradient(
                    colors = listOf(Color(0xFF3A7BFF).copy(alpha = 0.12f), Color.Transparent),
                    center = Offset(width * 0.98f, height * 0.36f),
                    radius = width * 0.72f,
                )
                val warmGlow = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFAA72).copy(alpha = 0.06f), Color.Transparent),
                    center = Offset(width * 0.46f, height * 1.03f),
                    radius = width * 0.7f,
                )
                val linePath = Path().apply {
                    moveTo(-20f, height * 0.72f)
                    cubicTo(width * 0.23f, height * 0.62f, width * 0.46f, height * 0.84f, width + 30f, height * 0.64f)
                }
                val secondLine = Path().apply {
                    moveTo(-20f, height * 0.79f)
                    cubicTo(width * 0.28f, height * 0.7f, width * 0.55f, height * 0.9f, width + 30f, height * 0.72f)
                }
                onDrawBehind {
                    drawRect(brush = glow)
                    drawRect(brush = blueGlow)
                    drawRect(brush = warmGlow)
                    drawPath(linePath, color = Color.White.copy(alpha = 0.026f), style = Stroke(1.dp.toPx()))
                    drawPath(secondLine, color = Color.White.copy(alpha = 0.018f), style = Stroke(1.dp.toPx()))
                    drawCircle(
                        color = accent.copy(alpha = 0.06f),
                        radius = width * 0.34f,
                        center = Offset(-width * 0.08f, height * 0.62f),
                    )
                    drawCircle(
                        color = Color(0xFF7EB8FF).copy(alpha = 0.035f),
                        radius = width * 0.29f,
                        center = Offset(width * 1.06f, height * 0.88f),
                    )
                }
            },
        content = content,
    )
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(28.dp),
    tint: Color = Color.White,
    borderAlpha: Float = 1f,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        tint.copy(alpha = 0.105f),
                        tint.copy(alpha = 0.042f),
                        tint.copy(alpha = 0.025f),
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.22f * borderAlpha),
                        Color.White.copy(alpha = 0.055f * borderAlpha),
                        Color.White.copy(alpha = 0.13f * borderAlpha),
                    )
                ),
                shape = shape,
            )
            .padding(contentPadding),
        content = content,
    )
}

@Composable
fun rememberPressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.955f,
): State<Float> {
    val pressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = tween(durationMillis = 140),
        label = "pressScale",
    )
}

@Composable
fun drawableIdFor(key: String): Int {
    val context = LocalContext.current
    return remember(key) {
        context.resources.getIdentifier(key, "drawable", context.packageName)
    }
}

@Composable
fun GlyphIcon(
    key: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Mist,
) {
    val id = drawableIdFor(key).takeIf { it != 0 } ?: R.drawable.ic_package
    Icon(
        painter = painterResource(id),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun LocalImage(
    path: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = Crop,
) {
    val image by produceState<ImageBitmap?>(initialValue = null, path) {
        value = withContext(Dispatchers.IO) {
            ImageStorage.decodeSampled(path, maxSide = 720)?.asImageBitmap()
        }
    }
    val bitmap = image
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale,
        )
    } else {
        Box(
            modifier = modifier.background(
                Brush.linearGradient(
                    listOf(Color(0xFF22323A), Color(0xFF14252C))
                )
            )
        )
    }
}

@Composable
fun CategoryGlyph(
    category: Category,
    modifier: Modifier = Modifier,
    tint: Color = Mist,
) {
    if (category.customIconPath != null) {
        LocalImage(
            path = category.customIconPath,
            modifier = modifier.clip(RoundedCornerShape(30.dp)),
        )
    } else {
        GlyphIcon(
            key = category.iconKey,
            contentDescription = category.name,
            modifier = modifier,
            tint = tint,
        )
    }
}

@Composable
fun IconChoice(
    option: IconOption,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(shape)
            .background(
                if (selected) accent.copy(alpha = 0.17f) else Color.White.copy(alpha = 0.045f)
            )
            .border(
                width = if (selected) 1.2.dp else 0.8.dp,
                color = if (selected) accent.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.09f),
                shape = shape,
            )
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        GlyphIcon(
            key = option.key,
            contentDescription = option.label,
            modifier = Modifier.size(23.dp),
            tint = if (selected) accent else Color.White.copy(alpha = 0.66f),
        )
    }
}

@Composable
fun CustomIconChoice(
    selected: Boolean,
    customIconPath: String?,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(shape)
            .background(
                if (selected) accent.copy(alpha = 0.17f) else Color.White.copy(alpha = 0.045f)
            )
            .border(
                width = if (selected) 1.2.dp else 0.8.dp,
                color = if (selected) accent.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.13f),
                shape = shape,
            )
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = "自定义图标" },
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
                    .padding(4.dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF071318).copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center,
            ) {
                GlyphIcon(
                    key = "ic_camera",
                    contentDescription = null,
                    modifier = Modifier.size(9.dp),
                    tint = Color.White.copy(alpha = 0.8f),
                )
            }
        } else {
            GlyphIcon(
                key = "ic_image_add",
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = if (selected) accent else Color.White.copy(alpha = 0.58f),
            )
        }
    }
}

@Composable
fun AccentButton(
    text: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(18.dp)
    val interaction = remember { MutableInteractionSource() }
    val scale by rememberPressScale(interaction, 0.97f)
    Box(
        modifier = modifier
            .height(54.dp)
            .scale(scale)
            .clip(shape)
            .background(
                if (enabled) {
                    Brush.linearGradient(
                        listOf(accent, accent.copy(alpha = 0.78f))
                    )
                } else {
                    Brush.linearGradient(listOf(Color(0xFF2A3C42), Color(0xFF213238)))
                }
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null) {
                GlyphIcon(
                    key = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF06231F),
                )
                androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) Color(0xFF06231F) else Color.White.copy(alpha = 0.35f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null,
    contentColor: Color = Color.White.copy(alpha = 0.82f),
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.045f))
            .border(0.8.dp, Color.White.copy(alpha = 0.09f), shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                GlyphIcon(
                    key = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor,
                )
                androidx.compose.foundation.layout.Spacer(Modifier.size(7.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun RoundIconButton(
    icon: String,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 46.dp,
    tint: Color = Color.White.copy(alpha = 0.82f),
    background: Color = Color.White.copy(alpha = 0.055f),
    borderColor: Color = Color.White.copy(alpha = 0.1f),
    iconSize: Dp = 19.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .border(0.8.dp, borderColor, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        GlyphIcon(
            key = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = tint,
        )
    }
}

@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = Color.White.copy(alpha = 0.38f),
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = accent,
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.35.sp,
    )
}

@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    accent: Color,
    buttonText: String,
    onButtonClick: () -> Unit,
    icon: String = "ic_package",
    modifier: Modifier = Modifier,
) {
    GlassPanel(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.12f))
                    .border(1.dp, accent.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                GlyphIcon(
                    key = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = accent,
                )
            }
            androidx.compose.foundation.layout.Spacer(Modifier.height(16.dp))
            Text(
                text = title,
                color = Mist,
                style = MaterialTheme.typography.titleMedium,
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.42f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            androidx.compose.foundation.layout.Spacer(Modifier.height(18.dp))
            GhostButton(
                text = buttonText,
                icon = "ic_add",
                onClick = onButtonClick,
                contentColor = accent,
            )
        }
    }
}
