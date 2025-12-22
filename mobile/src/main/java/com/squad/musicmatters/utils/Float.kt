package com.squad.musicmatters.utils

fun Float.toSafeFinite() = if ( !isFinite() ) 0f else this