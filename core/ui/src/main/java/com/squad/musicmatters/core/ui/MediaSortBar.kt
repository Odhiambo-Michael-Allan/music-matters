package com.squad.musicmatters.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.designsystem.R
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons

@Composable
fun <T : Enum<T>> MediaSortBar(
    sortInReverse: Boolean,
    onSortInReverseChange: (Boolean ) -> Unit,
    sortBy: T,
    sortTypes: Map<T, Int>,
    onSortTypeChange: ( T ) -> Unit,
    label: @Composable () -> Unit,
    onShufflePlay: ( () -> Unit )? = null
) {
    var showDropdownMenu by remember { mutableStateOf( false ) }

    Row (
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            Spacer( modifier = Modifier.width( 8.dp ) )
            IconButton(
                onClick = { onSortInReverseChange( !sortInReverse ) }
            ) {
                Icon(
                    modifier = Modifier.size( 20.dp ),
                    imageVector = if ( sortInReverse ) {
                        MusicMattersIcons.ArrowUpward
                    } else {
                        MusicMattersIcons.ArrowDownward
                    },
                    contentDescription = null
                )
            }
            Box {
                TextButton(
                    onClick = { showDropdownMenu = !showDropdownMenu }
                ) {
                    Text(
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        text = stringResource( id = sortTypes[ sortBy ]!! ),
                    )
                }
                DropdownMenu(
                    expanded = showDropdownMenu,
                    onDismissRequest = { showDropdownMenu = false }
                ) {
                    sortTypes.map {
                        val onClick = {
                            showDropdownMenu = false
                            onSortTypeChange( it.key )
                        }
                        DropdownMenuItem(
                            leadingIcon = {
                                RadioButton(
                                    selected = it.key == sortBy,
                                    onClick = onClick
                                )
                            },
                            text = {
                                Text(
                                    text = stringResource( id = it.value ),
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = onClick,
                        )
                    }
                }
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProvideTextStyle(
                value = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                label()
            }
            onShufflePlay?.let {
                IconButton(
                    modifier = Modifier.padding( 4.dp, 0.dp ),
                    onClick = it
                ) {
                    Icon(
                        painter = painterResource( id = R.drawable.ic_shuffle ),
                        contentDescription = null,
                        modifier = Modifier.size(
                            MusicMattersIcons.Shuffle.defaultHeight.minus( 5.dp )
                        )
                    )
                }
            } ?: Spacer( modifier = Modifier.width( 20.dp ) )
        }
    }
}
