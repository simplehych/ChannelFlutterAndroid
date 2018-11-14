package com.simple.channelflutterandroid

import android.app.Application
import android.content.Context

/**
 * @author hych
 * @date 2018/11/14 14:49
 */
class App : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }

}