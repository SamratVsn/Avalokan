package com.example.avalokan.ui.theme
import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,

    secondary = Sage,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = TextPrimary,

    tertiary = Terracotta,
    onTertiary = OnTertiary,
    tertiaryContainer = TerracottaContainer,
    onTertiaryContainer = TerracottaDark,

    error = DangerRed,
    onError = OnError,
    errorContainer = DangerBg,
    onErrorContainer = DangerRed,

    background = AppBackground,
    onBackground = TextPrimary,

    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,

    outline = OutlineMuted,
    outlineVariant = GreyBorder,

    inverseSurface = SurfaceDark,
    inverseOnSurface = TextOnDark,
    inversePrimary = SageLight,

    scrim = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = SageLight,
    onPrimary = Color(0xFF183B2B),
    primaryContainer = PrimaryGreen,
    onPrimaryContainer = Color(0xFFD8E8D9),

    secondary = Color(0xFFB1CBB8),
    onSecondary = Color(0xFF243D2E),
    secondaryContainer = Color(0xFF354E3D),
    onSecondaryContainer = Color(0xFFDCE8DD),

    tertiary = Color(0xFFE6A184),
    onTertiary = Color(0xFF482010),
    tertiaryContainer = Color(0xFF713D28),
    onTertiaryContainer = Color(0xFFFFDBCB),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = SurfaceDark,
    onBackground = Color(0xFFE4E9E0),

    surface = Color(0xFF1A2820),
    onSurface = Color(0xFFE4E9E0),
    surfaceVariant = Color(0xFF2C3B31),
    onSurfaceVariant = Color(0xFFC0CCC0),

    outline = Color(0xFF89978B),
    outlineVariant = Color(0xFF3C4B40),

    inverseSurface = AppBackground,
    inverseOnSurface = TextPrimary,
    inversePrimary = PrimaryGreen,

    scrim = Color.Black
)

@Composable
fun AvalokanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            // Intentionally use the Avalokan palette even when true.
            // Set dynamicColor behavior explicitly if it is needed later.
            if (darkTheme) DarkColorScheme else LightColorScheme
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()

            val insetsController =
                WindowCompat.getInsetsController(window, view)

            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AvalokanShapes,
        content = content
    )
}
