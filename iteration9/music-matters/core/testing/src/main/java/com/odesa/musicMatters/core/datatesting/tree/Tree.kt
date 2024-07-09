package com.odesa.musicMatters.core.datatesting.tree

import com.odesa.musicMatters.core.datatesting.songs.testSongs

val testPaths = listOf(
    "/storage/emulated/0/Music/Madeon/All My Friends/Madeon - All My Friends.mp3",
    "/storage/emulated/0/Music/Bea Miller/elated!/Bea Miller, Aminé - FEEL SOMETHING DIFFERENT.mp3",
    "/storage/emulated/0/Music/Tove Lo/Queen Of The Clouds/Tove Lo - Talking Body.mp3",
    "/storage/emulated/0/Music/Flume/Skin/Flume, Tove Lo - Say It.mp3",
    "/storage/emulated/0/Music/Sean Paul/I'm Still in Love with You/Sean Paul, Sasha - I'm Still in Love with You (feat. Sasha).mp3",
    "/storage/emulated/0/Music/Sean Paul/Tek Weh Yuh Heart/Sean Paul, Tory Lanez - Tek Weh Yuh Heart.mp3",
    "/storage/emulated/0/Music/DJ Snake/Carte Blanche/DJ Snake, J Balvin, Tyga - Loco Contigo.mp3",
    "/storage/emulated/0/Music/DJ Snake/Carte Blanche/DJ Snake - Magenta Riddim.mp3",
    "/storage/emulated/0/Music/DJ Snake/Carte Blanche/DJ Snake, Eptic - SouthSide.mp3",
    "/storage/emulated/0/Music/Sean Paul/Calling On Me/Sean Paul, Tove Lo - Calling On Me.mp3"
)

val testTreeMap = mapOf(
    testPaths[0] to testSongs.subList( 0, 5 ),
    testPaths[1] to testSongs.subList( 0, 7 ),
    testPaths[2] to testSongs.subList( 0, 4 ),
    testPaths[3] to testSongs.subList( 0, 4 ),
    testPaths[4] to testSongs.subList( 0, 6 ),
    testPaths[5] to testSongs.subList( 0, 3 )
)