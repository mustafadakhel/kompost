package com.dakhel.kompost.application

import android.util.Log

val kompostLogger: KompostLogger = DefaultLogger

internal fun toggleLogging(loggingEnabled: Boolean) {
    DefaultLogger.toggleLogging(loggingEnabled)
}

internal interface LoggingControl {
    fun toggleLogging(loggingEnabled: Boolean)
}

interface KompostLogger {
    fun log(message: String)
}

private object DefaultLogger : KompostLogger, LoggingControl {
    private var loggingEnabled: Boolean = false
    override fun log(message: String) {
        if (loggingEnabled) Log.d("KompostDebug", message)
    }

    override fun toggleLogging(loggingEnabled: Boolean) {
        this.loggingEnabled = loggingEnabled
    }
}
