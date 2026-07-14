package com.squad.musicmatters.feature.genres

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SortGenresBy
import com.squad.musicmatters.core.ui.IconTextBody
import com.squad.musicmatters.core.ui.LibraryDestinationContainer
import com.squad.musicmatters.core.ui.MediaSortBar
import com.squad.musicmatters.core.ui.MediaSortBarScaffold
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun GenresScreen(
    viewModel: GenresScreenViewModel = hiltViewModel(),
    onViewGenre: ( String ) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GenresScreenContent(
        uiState = uiState,
        onViewGenre = onViewGenre,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
        onSortGenresByChange = viewModel::onSortTypeChange,
        onSortGenresInReverseChange = viewModel::onSortInReverseChange
    )
}

@Composable
private fun GenresScreenContent(
    uiState: GenresScreenUiState,
    onSortGenresByChange: ( SortGenresBy ) -> Unit,
    onSortGenresInReverseChange: ( Boolean ) -> Unit,
    onViewGenre: ( String ) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {

    LibraryDestinationContainer(
        title = stringResource( id = i8nR.string.core_i8n_genres ),
        isLoading = uiState is GenresScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
    ) {
        when ( uiState ) {
            GenresScreenUiState.Loading -> {}
            is GenresScreenUiState.Success -> {
                GenresGrid(
                    genres = uiState.genres,
                    sortType = uiState.sortGenresBy,
                    sortReverse = uiState.sortGenresInReverse,
                    onSortTypeChange = onSortGenresByChange,
                    onSortReverseChange = onSortGenresInReverseChange,
                    onViewGenre = onViewGenre,
                )
            }
        }
    }

}

@Composable
fun GenresGrid(
    genres: List<Genre>,
    sortType: SortGenresBy,
    sortReverse: Boolean,
    onSortReverseChange: ( Boolean ) -> Unit,
    onSortTypeChange: ( SortGenresBy ) -> Unit,
    onViewGenre: ( String ) -> Unit,
) {
    MediaSortBarScaffold(
        mediaSortBar = {
            Box(
                modifier = Modifier.padding( bottom = 4.dp )
            ) {
                MediaSortBar(
                    sortInReverse = sortReverse,
                    onSortInReverseChange = onSortReverseChange,
                    sortBy = sortType,
                    sortTypes = SortGenresBy.entries.associateBy(
                        { it },
                        { it.label() }
                    ),
                    onSortTypeChange = onSortTypeChange,
                    label = {
                        Text(
                            text = stringResource(
                                id = if ( genres.size > 1 ) {
                                    i8nR.string.core_i8n_n_genres
                                } else {
                                    i8nR.string.core_i8n_genre
                                },
                                genres.size
                            ),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
            }
        }
    ) {
        when {
            genres.isEmpty() -> IconTextBody(
                icon = { modifier ->
                    Icon(
                        modifier = modifier,
                        imageVector = MusicMattersIcons.MusicNote,
                        contentDescription = null
                    )
                },
                content = {
                    Text( text = stringResource( id = i8nR.string.core_i8n_damn_this_is_so_empty ) )
                }
            )
            else -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive( 150.dp ),
                    horizontalArrangement = Arrangement.spacedBy( 4.dp ),
                    verticalItemSpacing = 4.dp,
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 70.dp
                    )
                ) {
                    itemsIndexed( genres ) { index, genre ->
                        GenreCard(
                            modifier = Modifier.animateItem(),
                            genre = genre,
                            position = index,
                            onClick = { onViewGenre( genre.name ) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun GenreCard(
    modifier: Modifier = Modifier,
    genre: Genre,
    position: Int,
    onClick: () -> Unit,
) {
    Card (
        modifier = modifier
            .padding( 2.dp ),
        colors = GenreTileColors.cardColors( index = position ),
        onClick = onClick,
    ) {
        val genreName = genre.name.takeIf { it.isNotBlank() }
            ?: stringResource( id = i8nR.string.core_i8n_untitled )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize( minHeight = 88.dp ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align( Alignment.BottomStart )
                    .matchParentSize()
                    .fillMaxWidth()
                    .alpha( 0.25f )
                    .absoluteOffset( 8.dp, 12.dp )
            ) {
                Text(
                    text = genreName,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.displaySmall
                        .copy( fontWeight = FontWeight.Bold ),
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )

            }
            Column(
                modifier = Modifier.padding( 20.dp ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = genreName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                        .copy( fontWeight = FontWeight.Bold )
                )
                Text(
                    text = stringResource(
                        id = if ( genre.numberOfTracks > 1 ) {
                            i8nR.string.core_i8n_n_songs
                        } else {
                            i8nR.string.core_i8n_one_song
                        },
                        genre.numberOfTracks
                    ),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

private object GenreTileColors {
    val colors = mutableListOf(
        0xFFEF4444,
        0xFFF97316,
        0xFFF59E0B,
        0xFF16A34A,
        0xFF06B6B4,
        0xFF8B5CF6,
        0xFFD946EF,
        0xFFF43F5E,
        0xFF6366F1,
        0xFFA855F7,
    ).map { Color( it ) }

    fun colorAt( index: Int ) = colors[ index % colors.size ]

    @Composable
    fun cardColors( index: Int ) = CardDefaults.cardColors(
        containerColor = colorAt( index ),
        contentColor = Color.White,
    )
}

private fun SortGenresBy.label(): Int =
    when ( this ) {
        SortGenresBy.NAME -> i8nR.string.core_i8n_title
        SortGenresBy.TRACK_COUNT -> i8nR.string.core_i8n_track_count
        SortGenresBy.CUSTOM -> i8nR.string.core_i8n_custom
    }

@PreviewScreenSizes
@Composable
private fun ArtistsScreenPreview(
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
        GenresScreenContent(
            uiState = GenresScreenUiState.Success(
                genres = previewData.genres,
//                genreResult = GenreResult.Success( previewData.genres ),
                sortGenresBy = SortGenresBy.NAME,
                sortGenresInReverse = false,
            ),
            onViewGenre = {},
            onSortGenresByChange = {},
            onSortGenresInReverseChange = {},
            onNavigateBack = {},
            onNavigateToSettings = {},
        )
    }
}