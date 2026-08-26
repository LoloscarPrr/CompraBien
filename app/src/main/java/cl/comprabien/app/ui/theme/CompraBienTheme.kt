package cl.comprabien.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CompraGreen = Color(0xFF2F7D5A)
val CompraGreenSoft = Color(0xFFDDF3E7)
val CompraBlue = Color(0xFF245B78)
val CompraBlueSoft = Color(0xFFDDECF4)
val CompraAmber = Color(0xFFE6A23C)
val CompraAmberSoft = Color(0xFFFFEBC8)
val CompraRed = Color(0xFFB94A48)
val CompraRedSoft = Color(0xFFF8DFDE)
val CompraInk = Color(0xFF1F2933)
val CompraMuted = Color(0xFF66727E)
val CompraBackground = Color(0xFFF7F8F5)
val CompraSurface = Color(0xFFFFFFFF)

private val CompraBienColors = lightColorScheme(
    primary = CompraGreen,
    onPrimary = Color.White,
    primaryContainer = CompraGreenSoft,
    onPrimaryContainer = CompraInk,
    secondary = CompraBlue,
    onSecondary = Color.White,
    secondaryContainer = CompraBlueSoft,
    onSecondaryContainer = CompraInk,
    tertiary = CompraAmber,
    onTertiary = CompraInk,
    tertiaryContainer = CompraAmberSoft,
    onTertiaryContainer = CompraInk,
    error = CompraRed,
    errorContainer = CompraRedSoft,
    background = CompraBackground,
    onBackground = CompraInk,
    surface = CompraSurface,
    onSurface = CompraInk,
    onSurfaceVariant = CompraMuted,
    outline = Color(0xFFB8C0C5)
)

@Composable
fun CompraBienTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CompraBienColors, content = content)
}
