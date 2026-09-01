package cl.comprabien.app.ui.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class LayoutProfile {
    COMPACT,
    NORMAL,
    LARGE
}

data class AdaptiveLayout(
    val profile: LayoutProfile,
    val horizontalPadding: Dp,
    val maxContentWidth: Dp,
    val categoryColumns: Int
) {
    val stackHomeActions: Boolean get() = profile == LayoutProfile.COMPACT
}

@Composable
fun rememberAdaptiveLayout(): AdaptiveLayout {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return when {
        widthDp < 360 -> AdaptiveLayout(
            profile = LayoutProfile.COMPACT,
            horizontalPadding = 12.dp,
            maxContentWidth = 600.dp,
            categoryColumns = 1
        )
        widthDp < 840 -> AdaptiveLayout(
            profile = LayoutProfile.NORMAL,
            horizontalPadding = 20.dp,
            maxContentWidth = 760.dp,
            categoryColumns = 2
        )
        else -> AdaptiveLayout(
            profile = LayoutProfile.LARGE,
            horizontalPadding = 32.dp,
            maxContentWidth = 1040.dp,
            categoryColumns = 3
        )
    }
}
