package com.squad.musicmatters.utils

fun <T> T.runFunctionIfTrueElseReturnThisObject(
    run: Boolean,
    fn: T.() -> T
) = when {
    run -> fn.invoke( this )
    else -> this
}