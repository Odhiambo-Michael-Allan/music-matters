package com.squad.musicmatters.core.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME )
annotation class Dispatcher( val dispatcher: MusicMattersDispatchers )

enum class MusicMattersDispatchers {
    Default,
    IO,
    Main,
}