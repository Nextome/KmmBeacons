package com.nextome.kmmbeacons

import android.content.Context
import androidx.startup.Initializer

internal lateinit var applicationContext: Context
    private set

public object KmmBeaconsContext

class KmmBeaconsInitializer: Initializer<KmmBeaconsContext> {
    override fun create(context: Context): KmmBeaconsContext {
        applicationContext = context.applicationContext
        return KmmBeaconsContext
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf()
    }

}