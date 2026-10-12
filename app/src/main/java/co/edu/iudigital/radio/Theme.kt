package co.edu.iudigital.radio.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryLight,
    secondary = PrimaryRed,
    tertiary = LightBlue,
    background = BackgroundDark,
    surface = SurfaceDark,
    onSurface = Color.White,
    onBackground = Color.White,
    outline = LightGray.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryLight,
    secondary = PrimaryRed,
    tertiary = LightBlue,
    background = BackgroundLight,
    surface = SurfaceLight,
    onSurface = Color.Black,
    onBackground = Color.Black,
    outline = DarkGray.copy(alpha = 0.5f)
)

@Composable
fun IUDigitalRadioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
