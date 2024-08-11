package com.odesa.musicMatters

import android.app.Application
import com.odesa.musicMatters.di.MobileDiModule

class MusicMatters : Application() {

    // AppContainer instance used by the rest of the classes to obtain dependencies
    lateinit var diModule: MobileDiModule

    override fun onCreate() {
        super.onCreate()
        diModule = MobileDiModule( applicationContext )
    }

}
