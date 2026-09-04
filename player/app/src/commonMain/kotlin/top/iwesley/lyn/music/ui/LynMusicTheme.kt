package top.iwesley.lyn.music.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import top.iwesley.lyn.music.core.model.AppThemePalette
import top.iwesley.lyn.music.core.model.AppThemeTextPalette
import top.iwesley.lyn.music.core.model.AppThemeTokens
import top.iwesley.lyn.music.core.model.CLASSIC_APP_THEME_TOKENS
import top.iwesley.lyn.music.core.model.deriveAppThemePalette

// 车机版：全局 Typography 缩放（影响设置/曲库/我的等非播放页面）
// 字号 ×1.5，行高 ×1.7（v4：行高比字号多放大一点，增加列表行间距/呼吸感）
// 播放页文字走 AutomotivePlayerUi 内的 AutomotiveTypography 基准 + scaleFont，
// 不受此处缩放影响（v3 曾因双重放大出 bug，v4 已修复）
private const val CarTypographyScale = 1.5f
private const val CarTypographyLineHeightScale = 1.7f

private fun scaledTypography(factor: Float, lineHeightFactor: Float): Typography {
    val base = Typography()
    fun TextStyle.scaled() = copy(
        fontSize = fontSize * factor,
        lineHeight = lineHeight * lineHeightFactor,
    )
    return Typography(
        displayLarge = base.displayLarge.scaled(),
        displayMedium = base.displayMedium.scaled(),
        displaySmall = base.displaySmall.scaled(),
        headlineLarge = base.headlineLarge.scaled(),
        headlineMedium = base.headlineMedium.scaled(),
        headlineSmall = base.headlineSmall.scaled(),
        titleLarge = base.titleLarge.scaled(),
        titleMedium = base.titleMedium.scaled(),
        titleSmall = base.titleSmall.scaled(),
        bodyLarge = base.bodyLarge.scaled(),
        bodyMedium = base.bodyMedium.scaled(),
        bodySmall = base.bodySmall.scaled(),
        labelLarge = base.labelLarge.scaled(),
        labelMedium = base.labelMedium.scaled(),
        labelSmall = base.labelSmall.scaled(),
    )
}

@Immutable
data class MainShellColors(
    val appGradientTop: Color,
    val navContainer: Color,
    val cardContainer: Color,
    val cardBorder: Color,
    val selectedContainer: Color,
    val selectedBorder: Color,
    val secondaryText: Color,
    val heroGlow: Color,
)

private val LocalMainShellColors = staticCompositionLocalOf {
    deriveAppThemePalette(
        tokens = CLASSIC_APP_THEME_TOKENS,
        textPalette = AppThemeTextPalette.White,
    ).toMainShellColors()
}

val mainShellColors: MainShellColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMainShellColors.current

@Composable
fun LynMusicTheme(
    themeTokens: AppThemeTokens = CLASSIC_APP_THEME_TOKENS,
    textPalette: AppThemeTextPalette = AppThemeTextPalette.White,
    content: @Composable () -> Unit,
) {
    val palette = remember(themeTokens, textPalette) {
        deriveAppThemePalette(
            tokens = themeTokens,
            textPalette = textPalette,
        )
    }
    val scaledTypo = remember { scaledTypography(CarTypographyScale, CarTypographyLineHeightScale) }
    MaterialTheme(
        colorScheme = palette.toColorScheme(),
        typography = scaledTypo,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides Color(palette.onBackgroundArgb),
            LocalMainShellColors provides palette.toMainShellColors(),
        ) {
            content()
        }
    }
}

val ColorScheme.heroGlow: Color
    @Composable
    @ReadOnlyComposable
    get() = mainShellColors.heroGlow

private fun AppThemePalette.toColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color(primaryArgb),
        onPrimary = Color(onPrimaryArgb),
        primaryContainer = Color(selectedContainerArgb),
        onPrimaryContainer = Color(onBackgroundArgb),
        secondary = Color(secondaryArgb),
        onSecondary = Color(onSecondaryArgb),
        secondaryContainer = Color(selectedContainerArgb),
        onSecondaryContainer = Color(onBackgroundArgb),
        tertiary = Color(tertiaryArgb),
        onTertiary = Color(onTertiaryArgb),
        tertiaryContainer = Color(cardContainerArgb),
        onTertiaryContainer = Color(onSurfaceArgb),
        background = Color(backgroundArgb),
        onBackground = Color(onBackgroundArgb),
        surface = Color(surfaceArgb),
        onSurface = Color(onSurfaceArgb),
        surfaceVariant = Color(surfaceVariantArgb),
        onSurfaceVariant = Color(onSurfaceVariantArgb),
        surfaceTint = Color(primaryArgb),
        inverseSurface = Color(onSurfaceArgb),
        inverseOnSurface = Color(surfaceArgb),
        inversePrimary = Color(secondaryArgb),
        outline = Color(outlineArgb),
        outlineVariant = Color(cardBorderArgb),
        scrim = Color(0x99000000.toInt()),
    )
}

private fun AppThemePalette.toMainShellColors(): MainShellColors {
    return MainShellColors(
        appGradientTop = Color(appGradientTopArgb),
        navContainer = Color(navContainerArgb),
        cardContainer = Color(cardContainerArgb),
        cardBorder = Color(cardBorderArgb),
        selectedContainer = Color(selectedContainerArgb),
        selectedBorder = Color(selectedBorderArgb),
        secondaryText = Color(secondaryTextArgb),
        heroGlow = Color(heroGlowArgb),
    )
}
