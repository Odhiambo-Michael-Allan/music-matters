package com.squad.musicmatters.feature.folders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ThumbUpAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicmatters.core.data.utils.VersionUtils
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.model.Folder
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.ScreenOrientation
import com.squad.musicmatters.core.ui.SongOptionsBottomSheetMenu
import com.squad.musicmatters.core.i8n.R as i8nR


@Composable
private fun FolderList(
    modifier: Modifier = Modifier,
    folders: List<Folder>,
    onViewFolder: ( String ) -> Unit,
) {
    
}

@Composable
private fun FolderCard(
    modifier: Modifier = Modifier,
    folder: Folder,
    onViewFolder: ( String ) -> Unit,
) {

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors( containerColor = Color.Transparent ),
        onClick = { onViewFolder( folder.path ) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 4.dp, 4.dp, 4.dp)
        ) {
            DynamicAsyncImage(
                imageUri = folder.artworkUri?.toUri(),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer( modifier = Modifier.width( 16.dp ) )
            Column( modifier = Modifier.weight( 1f ) ) {
                Text(
                    text = folder.path,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        id = if ( folder.trackCount > 1 ) {
                            i8nR.string.core_i8n_n_songs
                        } else {
                            i8nR.string.core_i8n_one_song
                        },
                        folder.trackCount
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
