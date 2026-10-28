package com.miraitowa.shiguang.ui.components

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

/**
 * Material3 modal sheets live in their own dialog window. Its default system bars follow the
 * platform light theme, which can produce a white navigation bar over our dark glass UI.
 * This keeps the bar transparent and the icons light, matching the main window.
 */
@Suppress("DEPRECATION")
@Composable
fun ConfigureSheetSystemBars() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            var parent = view.parent
            while (parent != null) {
                if (parent is DialogWindowProvider) {
                    val window = parent.window
                    window.navigationBarColor = android.graphics.Color.TRANSPARENT
                    window.statusBarColor = android.graphics.Color.TRANSPARENT
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        window.isNavigationBarContrastEnforced = false
                    }
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightNavigationBars = false
                        isAppearanceLightStatusBars = false
                    }
                    break
                }
                parent = parent.parent
            }
        }
    }
}
