package com.squad.musicmatters.ui.genres
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.squad.musicmatters.ui.components.GenreGrid
//import com.squad.musicmatters.ui.components.LoaderScaffold
//import com.squad.musicmatters.ui.components.TopAppBar
//
//@Composable
//fun GenresScreen(
//    viewModel: GenresScreenViewModel,
//    onGenreClick: ( String ) -> Unit,
//    onNavigateToSearch: () -> Unit,
//    onSettingsClicked: () -> Unit
//) {
//
//    val genreScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    GenresScreenContent(
//        uiState = genreScreenUiState,
//        onGenreClick = onGenreClick,
//        onNavigateToSearch = onNavigateToSearch,
//        onSettingsClicked = onSettingsClicked,
//        onSortReverseChange = viewModel::setSortGenresInReverse,
//        onSortTypeChange = viewModel::setSortGenresBy
//    )
//}
//
//@Composable
//fun GenresScreenContent(
//    uiState: GenreScreenUiState,
//    onGenreClick: ( String ) -> Unit,
//    onNavigateToSearch: () -> Unit,
//    onSettingsClicked: () -> Unit,
//    onSortTypeChange: ( SortGenresBy ) -> Unit,
//    onSortReverseChange: ( Boolean ) -> Unit,
//) {
//    Column (
//        modifier = Modifier.fillMaxSize()
//    ) {
//        TopAppBar(
//            onNavigationIconClicked = onNavigateToSearch,
//            title = uiState.language.genres,
//            settings = uiState.language.settings,
//            onSettingsClicked = onSettingsClicked
//        )
//        LoaderScaffold(
//            isLoading = uiState.isLoading,
//            loading = uiState.language.loading
//        ) {
//            GenreGrid(
//                genres = uiState.genres,
//                language = uiState.language,
//                sortType = uiState.sortGenresBy,
//                sortReverse = uiState.sortGenresInReverse,
//                onSortReverseChange = onSortReverseChange,
//                onSortTypeChange = onSortTypeChange,
//                onGenreClick = onGenreClick
//            )
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun GenresScreenContentPreview() {
//    GenresScreenContent(
//        uiState = testGenreScreenUiState,
//        onGenreClick = {},
//        onNavigateToSearch = {},
//        onSettingsClicked = {},
//        onSortReverseChange = {},
//        onSortTypeChange = {}
//    )
//}
