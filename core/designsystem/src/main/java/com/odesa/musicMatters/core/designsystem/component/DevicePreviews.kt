package com.odesa.musicMatters.core.designsystem.component

import androidx.compose.ui.tooling.preview.Preview

/**
 * Multi-preview annotation that represents various device sizes. Add this annotation to a
 * composable to render various devices.
 */
@Preview( name = "phone", device = "spec:width=360dp,height=640dp,dpi=480", showBackground = true )
@Preview( name = "large-phone", device = "spec:width=360dp,height=740dp,dpi=480", showBackground = true )
@Preview( name = "landscape", device = "spec:width=640dp,height=360dp,dpi=480", showBackground = true )
@Preview( name = "foldable", device = "spec:width=673dp,height=841dp,dpi=480", showBackground = true )
@Preview( name = "tablet", device = "spec:width=1280dp,height=800dp,dpi=480", showBackground = true )
annotation class DevicePreviews
