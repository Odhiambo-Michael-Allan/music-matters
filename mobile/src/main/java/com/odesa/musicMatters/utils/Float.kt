package com.odesa.musicMatters.utils

fun Float.toSafeFinite() = if ( !isFinite() ) 0f else this