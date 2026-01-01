package com.squad.musicmatters.core.ui.dialog

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.media.media.extensions.dateModifiedString
import com.squad.musicmatters.core.media.media.extensions.sizeString
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.ui.PreviewParameterData


@SuppressLint( "UnusedBoxWithConstraintsScope" )
@Composable
fun SongDetailsDialog(
    song: Song,
    metadata: SongAdditionalMetadata?,
    language: Language,
    durationFormatter: ( Long ) -> String,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card (
            shape = RoundedCornerShape( 16.dp )
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding( 8.dp )
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = language.details,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                HorizontalDivider()
                Column (
                    modifier = Modifier
                        .verticalScroll( rememberScrollState() )
                        .padding( 0.dp, 4.dp )
                ) {

                    SongDetailsItem(
                        key = language.trackName,
                        value = song.title,
                        language = language
                    )

                    SongDetailsItem(
                        key = language.artist,
                        value = song.artists.joinToString(),
                        language = language
                    )

                    song.albumTitle?.let {
                        SongDetailsItem(
                            key = language.album,
                            value = it,
                            language = language
                        )
                    }

                    song.composer?.let {
                        SongDetailsItem(
                            key = language.composer,
                            value = it,
                            language = language
                        )
                    }

                    metadata?.genre?.let {
                        SongDetailsItem(
                            key = language.genre,
                            value = it,
                            language = language,
                        )
                    }

                    song.year?.let {
                        SongDetailsItem(
                            key = language.year,
                            value = it.toString(),
                            language = language
                        )
                    }

                    song.trackNumber?.let {
                        SongDetailsItem(
                            key = language.trackNumber,
                            value = ( it % 1000 ).toString(),
                            language = language
                        )
                    }

                    SongDetailsItem(
                        key = language.duration,
                        value = durationFormatter( song.duration ),
                        language = language
                    )

                    SongDetailsItem(
                        key = language.path,
                        value = song.path,
                        language = language
                    )

                    SongDetailsItem(
                        key = language.size,
                        value = song.sizeString,
                        language = language
                    )

                    SongDetailsItem(
                        key = language.dateAdded,
                        value = song.dateModifiedString,
                        language = language
                    )

                    metadata?.bitrate?.let {
                        SongDetailsItem(
                            key = language.bitrate,
                            value = language.xKbps( it.toString() ),
                            language = language,
                        )
                    }

                    metadata?.bitsPerSample?.let {
                        SongDetailsItem(
                            key = language.bitDepth,
                            value = language.xBit( it.toString() ),
                            language = language,
                        )
                    }

                    metadata?.samplingRate?.let {
                        SongDetailsItem(
                            key = language.samplingRate,
                            value = language.xKHZ( it.toString() ),
                            language = language,
                        )
                    }

                    metadata?.codec?.let {
                        SongDetailsItem(
                            key = language.codec,
                            value = it,
                            language = language,
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun SongDetailsItem(
    key: String,
    value: String,
    language: Language
) {
    Column (
        modifier = Modifier.padding( 0.dp, 4.dp )
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview
@Composable
fun SongDetailsDialogPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true
    ) {
        SongDetailsDialog(
            song = PreviewParameterData.songs.first(),
            language = English,
            durationFormatter = { "3:44" },
            metadata = SongAdditionalMetadata(
                songId = "",
                codec = "unknown",
                bitrate = 0,
                samplingRate = 0f,
                bitsPerSample = 0,
                genre = "unknown"
            ),
            onDismissRequest = {}
        )
    }
}