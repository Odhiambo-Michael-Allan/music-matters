package com.odesa.musicMatters.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.core.data.preferences.SortGenresBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.datatesting.genres.testGenres
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.Genre

private object GenreTile {
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

@Composable
fun GenreGrid(
    genres: List<Genre>,
    language: Language,
    sortType: SortGenresBy,
    sortReverse: Boolean,
    onSortReverseChange: ( Boolean ) -> Unit,
    onSortTypeChange: (SortGenresBy ) -> Unit,
    onGenreClick: ( String ) -> Unit,
) {
    MediaSortBarScaffold(
        mediaSortBar = {
            Box(
                modifier = Modifier.padding( bottom = 4.dp )
            ) {
                MediaSortBar(
                    sortReverse = sortReverse,
                    onSortReverseChange = onSortReverseChange,
                    sortType = sortType,
                    sortTypes = SortGenresBy.entries.associateBy( { it }, { it.sortGenresByLabel( language ) } ),
                    onSortTypeChange = onSortTypeChange,
                    label = {
                        Text(
                            text = language.xGenres(
                                genres.size.toString()
                            )
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
                        imageVector = Icons.Filled.MusicNote,
                        contentDescription = null
                    )
                },
                content = {
                    Text( language.damnThisIsSoEmpty )
                }
            )
            else -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive( 150.dp ),
                    horizontalArrangement = Arrangement.spacedBy( 4.dp ),
                    verticalItemSpacing = 4.dp,
                    contentPadding = PaddingValues( 8.dp )
                ) {
                    itemsIndexed( genres ) { index, genre ->
                        GenreCard(
                            genre = genre,
                            position = index,
                            onClick = { onGenreClick( genre.name ) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun GenreCard(
    genre: Genre,
    position: Int,
    onClick: () -> Unit,
) {
    Card (
        modifier = Modifier
            .padding( 2.dp ),
        colors = GenreTile.cardColors( index = position ),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(minHeight = 88.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .matchParentSize()
                    .fillMaxWidth()
                    .alpha(0.25f)
                    .absoluteOffset(8.dp, 12.dp)
            ) {
                Text(
                    text = genre.name,
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
                    text = genre.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                        .copy( fontWeight = FontWeight.Bold )
                )
                Text(
                    text = genre.numberOfTracks.toString(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Preview( showBackground = true )
@Composable
fun GenreCardPreview() {
    GenreCard(
        genre = testGenres.first(),
        position = 0,
        onClick = {}
    )
}



@Preview( showSystemUi = true )
@Composable
fun GenreGridPreview() {
    GenreGrid(
        genres = testGenres,
        language = SettingsDefaults.language,
        sortType = SortGenresBy.NAME,
        sortReverse = false,
        onSortReverseChange = {},
        onSortTypeChange = {},
        onGenreClick = {}
    )
}

fun SortGenresBy.sortGenresByLabel(language: Language ) = when ( this ) {
    SortGenresBy.NAME -> language.genre
    SortGenresBy.CUSTOM -> language.custom
    SortGenresBy.TRACKS_COUNT -> language.trackCount
}

