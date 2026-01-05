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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.squad.musicMatters.core.i8n.R
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.media.media.extensions.dateModifiedString
import com.squad.musicmatters.core.media.media.extensions.sizeString
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.ui.PreviewParameterData


@SuppressLint( "UnusedBoxWithConstraintsScope" )
@Composable
fun SongDetailsDialog(
    song: Song,
    metadata: SongAdditionalMetadata?,
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
                        text = stringResource( id = R.string.core_i8n_details ),
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
                        key = stringResource( id = R.string.core_i8n_track_name ),
                        value = song.title,
                    )

                    SongDetailsItem(
                        key = stringResource( id = R.string.core_i8n_artist ),
                        value = song.artists.joinToString(),
                    )

                    song.albumTitle?.let {
                        SongDetailsItem(
                            key = stringResource( id = R.string.core_i8n_album ),
                            value = it,
                        )
                    }

                    song.composer?.let {
                        SongDetailsItem(
                            key = stringResource( id = R.string.core_i8n_composer ),
                            value = it,
                        )
                    }

                    metadata?.genre?.let {
                        SongDetailsItem(
                            key = stringResource( id = R.string.core_i8n_genre ),
                            value = it,
                        )
                    }

                    song.year?.let {
                        SongDetailsItem(
                            key = stringResource( id = R.string.core_i8n_year ),
                            value = it.toString(),
                        )
                    }

                    song.trackNumber?.let {
                        SongDetailsItem(
                            key = stringResource( id = R.string.core_i8n_track_number ),
                            value = ( it % 1000 ).toString(),
                        )
                    }

                    SongDetailsItem(
                        key = stringResource( id = R.string.core_i8n_duration ),
                        value = durationFormatter( song.duration ),
                    )

                    SongDetailsItem(
                        key = stringResource( id = R.string.core_i8n_path ),
                        value = song.path,
                    )

                    SongDetailsItem(
                        key = stringResource( id = R.string.core_i8n_size ),
                        value = song.sizeString,
                    )

                    SongDetailsItem(
                        key = stringResource( id = R.string.core_i8n_date_added ),
                        value = song.dateModifiedString,
                    )

                    metadata?.bitrate?.let {
                        SongDetailsItem(
                            key = stringResource( id = R.string.core_i8n_bitrate ),
                            value = stringResource(
                                id = R.string.core_i8n_xKbps,
                                it.toString()
                            )
                        )
                    }

                    metadata?.bitsPerSample?.let {
                        SongDetailsItem(
                            key = stringResource( id = R.string.core_i8n_bits_per_sample ),
                            value = stringResource(
                                id = R.string.core_i8n_xBit,
                                it.toString()
                            ),
                        )
                    }

                    metadata?.samplingRate?.let {
                        SongDetailsItem(
                            key = stringResource( id = R.string.core_i8n_sampling_rate ),
                            value = stringResource(
                                id = R.string.core_i8n_xKHZ,
                                ( it.div(1000 ) ).toString()
                            )
                        )
                    }

                    metadata?.codec?.let {
                        SongDetailsItem(
                            key = stringResource( id = R.string.core_i8n_codec ),
                            value = it,
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