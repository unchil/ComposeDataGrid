package com.unchil.composedatagrid.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import composedatagrid.composeapp.generated.resources.AppleGothic
import composedatagrid.composeapp.generated.resources.Res
import composedatagrid.composeapp.generated.resources.AppleSDGothicNeo


@Composable
fun AppTheme(enableDarkMode: Boolean = false, content: @Composable () -> Unit) {

    val AppleGothic = FontFamily(
        Font(Res.font.AppleGothic, FontWeight.Bold),
        Font(Res.font.AppleGothic, FontWeight.SemiBold),
        Font(Res.font.AppleGothic, FontWeight.ExtraBold),
        Font(Res.font.AppleGothic, FontWeight.Medium),
        Font(Res.font.AppleGothic, FontWeight.Normal),
        Font(Res.font.AppleGothic, FontWeight.Black),
        Font(Res.font.AppleGothic, FontWeight.Thin),
        Font(Res.font.AppleGothic, FontWeight.ExtraLight),
        Font(Res.font.AppleGothic, FontWeight.Light)
    )

    val AppleSDGothicNeo = FontFamily(
        Font(Res.font.AppleSDGothicNeo, FontWeight.Bold),
        Font(Res.font.AppleSDGothicNeo, FontWeight.SemiBold),
        Font(Res.font.AppleSDGothicNeo, FontWeight.ExtraBold),
        Font(Res.font.AppleSDGothicNeo, FontWeight.Medium),
        Font(Res.font.AppleSDGothicNeo, FontWeight.Normal),
        Font(Res.font.AppleSDGothicNeo, FontWeight.Black),
        Font(Res.font.AppleSDGothicNeo, FontWeight.Thin),
        Font(Res.font.AppleSDGothicNeo, FontWeight.ExtraLight),
        Font(Res.font.AppleSDGothicNeo, FontWeight.Light)
    )




    val typography = Typography(
        displayLarge = Typography().displayLarge.copy(fontFamily = AppleSDGothicNeo),
        displayMedium = Typography().displayMedium.copy(fontFamily = AppleSDGothicNeo),
        displaySmall = Typography().displaySmall.copy(fontFamily = AppleSDGothicNeo),
        headlineLarge = Typography().headlineLarge.copy(fontFamily = AppleSDGothicNeo),
        headlineMedium = Typography().headlineMedium.copy(fontFamily = AppleSDGothicNeo),
        headlineSmall = Typography().headlineSmall.copy(fontFamily = AppleSDGothicNeo),
        titleLarge = Typography().titleLarge.copy(fontFamily = AppleSDGothicNeo),
        titleMedium = Typography().titleMedium.copy(fontFamily = AppleSDGothicNeo),
        titleSmall = Typography().titleSmall.copy(fontFamily = AppleSDGothicNeo),
        bodyLarge = Typography().bodyLarge.copy(fontFamily = AppleSDGothicNeo),
        bodyMedium = Typography().bodyMedium.copy(fontFamily = AppleSDGothicNeo),
        bodySmall = Typography().bodySmall.copy(fontFamily = AppleSDGothicNeo),
        labelLarge = Typography().labelLarge.copy(fontFamily = AppleSDGothicNeo),
        labelMedium = Typography().labelMedium.copy(fontFamily = AppleSDGothicNeo),
        labelSmall = Typography().labelSmall.copy(fontFamily = AppleSDGothicNeo)
    )


    val LightColors = lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        tertiary = md_theme_light_tertiary,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        error = md_theme_light_error,
        onError = md_theme_light_onError,
        errorContainer = md_theme_light_errorContainer,
        onErrorContainer = md_theme_light_onErrorContainer,
        outline = md_theme_light_outline,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        inverseSurface = md_theme_light_inverseSurface,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inversePrimary = md_theme_light_inversePrimary,
        surfaceTint = md_theme_light_surfaceTint,
        outlineVariant = md_theme_light_outlineVariant,
        scrim = md_theme_light_scrim,
    )


    val DarkColors = darkColorScheme(
        primary = md_theme_dark_primary,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        onPrimaryContainer = md_theme_dark_onPrimaryContainer,
        secondary = md_theme_dark_secondary,
        onSecondary = md_theme_dark_onSecondary,
        secondaryContainer = md_theme_dark_secondaryContainer,
        onSecondaryContainer = md_theme_dark_onSecondaryContainer,
        tertiary = md_theme_dark_tertiary,
        onTertiary = md_theme_dark_onTertiary,
        tertiaryContainer = md_theme_dark_tertiaryContainer,
        onTertiaryContainer = md_theme_dark_onTertiaryContainer,
        error = md_theme_dark_error,
        onError = md_theme_dark_onError,
        errorContainer = md_theme_dark_errorContainer,
        onErrorContainer = md_theme_dark_onErrorContainer,
        outline = md_theme_dark_outline,
        background = md_theme_dark_background,
        onBackground = md_theme_dark_onBackground,
        surface = md_theme_dark_surface,
        onSurface = md_theme_dark_onSurface,
        surfaceVariant = md_theme_dark_surfaceVariant,
        onSurfaceVariant = md_theme_dark_onSurfaceVariant,
        inverseSurface = md_theme_dark_inverseSurface,
        inverseOnSurface = md_theme_dark_inverseOnSurface,
        inversePrimary = md_theme_dark_inversePrimary,
        surfaceTint = md_theme_dark_surfaceTint,
        outlineVariant = md_theme_dark_outlineVariant,
        scrim = md_theme_dark_scrim,
    )


    val colorScheme = if(enableDarkMode) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography
    ) {
        content()
    }

}