package com.odesa.musicMatters.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest


@Composable
fun BottomSheetMenuContent(
    bottomSheetHeader: @Composable () -> Unit,
    bottomSheetContent: @Composable () -> Unit,
) {
    Column (
        modifier = Modifier.verticalScroll( rememberScrollState() )
    ) {
        bottomSheetHeader()
        HorizontalDivider( modifier = Modifier.padding( 32.dp, 0.dp ) )
        bottomSheetContent()
    }
}

@Composable
fun BottomSheetMenuHeader(
    headerImage: ImageRequest?,
    title: String,
    titleIsHighlighted: Boolean = false,
    description: String,
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp, 16.dp),
        horizontalArrangement = Arrangement.spacedBy( 24.dp ),
    ) {
        headerImage?.let {
            AsyncImage(
                model = headerImage,
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentDescription = null
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = when {
                        titleIsHighlighted -> MaterialTheme.colorScheme.primary
                        else -> LocalTextStyle.current.color
                    }
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BottomSheetMenuItem(
    leadingIcon: ImageVector,
    leadingIconTint: Color? = null,
    leadingIconContentDescription: String = "",
    label: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = if ( isSelected ) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
        ),
        shape = RoundedCornerShape( 32.dp ),
        onClick = onClick
    ) {
        Row (
            modifier = Modifier.padding( 16.dp ),
            horizontalArrangement = Arrangement.spacedBy( 24.dp ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = leadingIcon,
                tint = leadingIconTint ?: LocalContentColor.current,
                contentDescription = leadingIconContentDescription
            )
            Text(
                text = label
            )
        }
    }
}