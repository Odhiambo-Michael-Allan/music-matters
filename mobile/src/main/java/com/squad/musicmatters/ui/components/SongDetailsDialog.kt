package com.squad.musicmatters.ui.components
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.BoxWithConstraints
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Card
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Dialog
//import com.squad.musicmatters.core.media.media.extensions.dateModifiedString
//import com.squad.musicmatters.core.media.media.extensions.sizeString
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.core.model.SongAdditionalMetadata
//
//
//@Composable
//fun SongDetailsDialog(
//    song: Song,
//    language: Language,
//    durationFormatter: ( Long ) -> String,
//    isLoadingSongAdditionalMetadata: Boolean,
//    onGetSongAdditionalMetadata: () -> SongAdditionalMetadata?,
//    onDismissRequest: () -> Unit,
//) {
//    Dialog(
//        onDismissRequest = onDismissRequest
//    ) {
//        BoxWithConstraints {
//            val dialogWidth = maxWidth * 0.85f
//            val dialogHeight = maxHeight * 0.85f
//
//            Card (
//                modifier = Modifier
//                    .width(dialogWidth)
//                    .height(dialogHeight),
//                shape = RoundedCornerShape( 16.dp )
//            ) {
//                Column (
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(8.dp)
//                ) {
//                    Row (
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp),
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        Text(
//                            text = language.details,
//                            style = MaterialTheme.typography.titleSmall.copy(
//                                fontWeight = FontWeight.Bold
//                            )
//                        )
//                    }
//                    HorizontalDivider()
//                    Column (
//                        modifier = Modifier
//                            .verticalScroll( rememberScrollState() )
//                            .padding(0.dp, 4.dp)
//                    ) {
//                        SongDetailsItem(
//                            key = language.trackName,
//                            value = song.title,
//                            language = language
//                        )
//                        SongDetailsItem(
//                            key = language.artist,
//                            value = song.artists.joinToString(),
//                            language = language
//                        )
//                        song.albumTitle?.let {
//                            SongDetailsItem(
//                                key = language.album,
//                                value = it,
//                                language = language
//                            )
//                        }
//                        song.composer?.let {
//                            SongDetailsItem(
//                                key = language.composer,
//                                value = it,
//                                language = language
//                            )
//                        }
//                        SongDetailsItem(
//                            key = language.genre,
//                            value = onGetSongAdditionalMetadata()?.genre ?: "",
//                            language = language,
//                            isLoadingValue = isLoadingSongAdditionalMetadata
//                        )
//                        song.year?.let {
//                            SongDetailsItem(
//                                key = language.year,
//                                value = it.toString(),
//                                language = language
//                            )
//                        }
//                        song.trackNumber?.let {
//                            SongDetailsItem(
//                                key = language.trackNumber,
//                                value = ( it % 1000 ).toString(),
//                                language = language
//                            )
//                        }
//                        SongDetailsItem(
//                            key = language.duration,
//                            value = durationFormatter( song.duration ),
//                            language = language
//                        )
//                        SongDetailsItem(
//                            key = language.path,
//                            value = song.path,
//                            language = language
//                        )
//                        SongDetailsItem(
//                            key = language.size,
//                            value = song.sizeString,
//                            language = language
//                        )
//                        SongDetailsItem(
//                            key = language.dateAdded,
//                            value = song.dateModifiedString,
//                            language = language
//                        )
//                        SongDetailsItem(
//                            key = language.bitrate,
//                            value = onGetSongAdditionalMetadata()?.let { language.xKbps( it.bitrate.toString() ) } ?: "",
//                            language = language,
//                            isLoadingValue = isLoadingSongAdditionalMetadata
//                        )
//                        SongDetailsItem(
//                            key = language.bitDepth,
//                            value = onGetSongAdditionalMetadata()?.let { language.xBit( it.bitsPerSample.toString() ) } ?: "",
//                            language = language,
//                            isLoadingValue = isLoadingSongAdditionalMetadata
//                        )
//                        SongDetailsItem(
//                            key = language.samplingRate,
//                            value = onGetSongAdditionalMetadata()?.let { language.xKHZ( it.samplingRate.toString() ) } ?: "",
//                            language = language,
//                            isLoadingValue = isLoadingSongAdditionalMetadata
//                        )
//                        SongDetailsItem(
//                            key = language.codec,
//                            value = onGetSongAdditionalMetadata()?.codec ?: "",
//                            language = language,
//                            isLoadingValue = isLoadingSongAdditionalMetadata
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SongDetailsItem(
//    key: String,
//    value: String,
//    language: Language,
//    isLoadingValue: Boolean = false
//) {
//    Column (
//        modifier = Modifier.padding( 0.dp, 4.dp )
//    ) {
//        Text(
//            text = key,
//            style = MaterialTheme.typography.labelSmall.copy(
//                fontWeight = FontWeight.SemiBold
//            )
//        )
//        if ( isLoadingValue ) {
//            Row (
//                horizontalArrangement = Arrangement.spacedBy( 4.dp ),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                CircularProgressIndicator(
//                    strokeWidth = 2.dp,
//                    modifier = Modifier.size( 12.dp )
//                )
//                Text(
//                    text = language.loading,
//                    style = MaterialTheme.typography.labelMedium
//                )
//            }
//        } else {
//            Text(
//                text = value,
//                style = MaterialTheme.typography.labelMedium
//            )
//        }
//    }
//}
//
////@Preview( showSystemUi = true )
////@Composable
////fun SongDetailsDialogPreview() {
////    MusicMattersTheme(
////        themeMode = SettingsDefaults.themeMode,
////        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
////        fontName = SettingsDefaults.font,
////        fontScale = SettingsDefaults.FONT_SCALE,
////        useMaterialYou = true
////    ) {
////        SongDetailsDialog(
////            song = testSongs.first(),
////            language = English,
////            durationFormatter = { "3:44" },
////            isLoadingSongAdditionalMetadata = true,
////            onGetSongAdditionalMetadata = {
////                SongAdditionalMetadataInfo(
////                    songId = "",
////                    codec = "unknown",
////                    bitrate = 5,
////                    samplingRate = 10f,
////                    bitsPerSample = 15,
////                    genre = "unknown"
////                )
////            },
////            onDismissRequest = {}
////        )
////    }
////}