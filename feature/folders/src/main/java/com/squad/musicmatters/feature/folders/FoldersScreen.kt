package com.squad.musicmatters.feature.folders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Folder
import com.squad.musicmatters.core.model.SortPathsBy
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.IconTextBody
import com.squad.musicmatters.core.ui.LibraryDestinationContainer
import com.squad.musicmatters.core.ui.MediaSortBar
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.i8n.R as i8nR
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer

@Composable
internal fun FoldersScreen(
    viewModel: FoldersScreenViewModel = hiltViewModel(),
    onViewFolder: ( String ) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FoldersScreenContent(
        uiState = uiState,
        onViewFolder = onViewFolder,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
        onSortTypeChange = viewModel::setSortPaths,
        onSortInReverseChange = viewModel::setSortPathsIn
    )

}

@Composable
private fun FoldersScreenContent(
    uiState: FoldersScreenUiState,
    onSortTypeChange: ( SortPathsBy ) -> Unit,
    onSortInReverseChange: ( Boolean ) -> Unit,
    onViewFolder: ( String ) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    LibraryDestinationContainer(
        title = stringResource( id = i8nR.string.core_i8n_folders ),
        isLoading = uiState is FoldersScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
    ) {
        when ( uiState ) {
            FoldersScreenUiState.Loading -> {}
            is FoldersScreenUiState.Success -> {
                FoldersList(
                    folders = uiState.folders,
                    sortBy = uiState.sortPathsBy,
                    sortInReverse = uiState.sortPathsInReverse,
                    onSortTypeChange = onSortTypeChange,
                    onSortInReverseChange = onSortInReverseChange,
                    onViewFolder = onViewFolder,
                )
            }
        }
    }
}

@Composable
private fun FoldersList(
    modifier: Modifier = Modifier,
    folders: List<Folder>,
    sortBy: SortPathsBy,
    sortInReverse: Boolean,
    onSortTypeChange: ( SortPathsBy ) -> Unit,
    onSortInReverseChange: ( Boolean ) -> Unit,
    onViewFolder: ( String ) -> Unit,
) {
    Column {
        MediaSortBar(
            sortBy = sortBy,
            sortInReverse = sortInReverse,
            sortTypes = SortPathsBy.entries.associateBy(
                { it },
                { it.label() }
            ),
            onSortTypeChange = onSortTypeChange,
            onSortInReverseChange = onSortInReverseChange,
            label = {
                Text(
                    text = stringResource(
                        id = if ( folders.size > 1 ) {
                            i8nR.string.core_i8n_n_folders
                        } else {
                            i8nR.string.core_i8n_one_folder
                        },
                        folders.size
                    ),
                    fontWeight = FontWeight.Bold
                )
            }
        )

        when {
            folders.isEmpty() -> {
                IconTextBody(
                    icon = { modifier ->
                        Icon(
                            modifier = modifier,
                            imageVector = MusicMattersIcons.Folder,
                            contentDescription = null
                        )
                    }
                ) {
                    Text(
                        text = stringResource( id = i8nR.string.core_i8n_damn_this_is_so_empty ),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues( bottom = 70.dp ),
                    modifier = modifier,
                ) {
                    items(
                        items = folders,
                        key = { it.path },
                    ) { folder ->
                        FolderCard(
                            modifier = Modifier.animateItem(),
                            folder = folder,
                            onViewFolder = onViewFolder,
                        )
                    }
                }
            }
        }


    }
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
                .padding(12.dp, 4.dp )
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
                    text = folder.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                MiddleEllipsisText(
                    text = folder.path,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                    )
                )
            }
        }
    }
}




@Composable
fun MiddleEllipsisText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default
) {
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints( modifier = modifier ) {
        val density = LocalDensity.current
        // Convert the maximum available layout width from Dp to pixels
        val maxWidthPx = with( density ) { maxWidth.toPx() }

        // Measure the full text width in pixels
        val fullTextWidth = textMeasurer.measure( text = text, style = style ).size.width

        // If it fits or is too short to split, keep it as is
        val finalizedText = if ( fullTextWidth <= maxWidthPx || text.length <= 4 ) {
            text
        } else {
            var result = text
            val ellipsis = "..."

            var startLen = text.length / 2
            var endLen = text.length - startLen

            // Trim from the middle outward until the string fits the layout constraints
            while ( startLen > 0 && endLen > 0 ) {
                val proposedText = text.take( startLen ) + ellipsis +
                        text.takeLast( endLen )
                val proposedWidth = textMeasurer.measure(
                    text = proposedText,
                    style = style
                ).size.width

                if ( proposedWidth <= maxWidthPx ) {
                    result = proposedText
                    break
                }

                // Alternate shrinking the start and end pieces
                if ( startLen >= endLen ) {
                    startLen--
                } else {
                    endLen--
                }
            }
            result
        }

        Text(
            text = finalizedText,
            style = style,
            maxLines = 1,
            // Prevent the system from adding another ellipsis at the end
            overflow = TextOverflow.Clip
        )
    }
}

private fun SortPathsBy.label() = when ( this ) {
    SortPathsBy.NAME -> i8nR.string.core_i8n_title
    SortPathsBy.TRACK_COUNT -> i8nR.string.core_i8n_track_count
    SortPathsBy.CUSTOM -> i8nR.string.core_i8n_custom
}

@PreviewScreenSizes
@Composable
private fun FoldersScreenContentPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        FoldersScreenContent(
            uiState = FoldersScreenUiState.Success(
                folders = previewData.folders,
                sortPathsBy = SortPathsBy.NAME,
                sortPathsInReverse = false
            ),
            onViewFolder = {},
            onNavigateBack = {},
            onSortTypeChange = {},
            onSortInReverseChange = {},
            onNavigateToSettings = {},
        )
    }
}
