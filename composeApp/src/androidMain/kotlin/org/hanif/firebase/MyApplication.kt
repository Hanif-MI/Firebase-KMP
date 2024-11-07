package org.hanif.firebase

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        multiplatform.network.cmptoast.AppContext.apply { set(applicationContext) }
    }
}