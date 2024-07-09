package com.mustafadakhel.kompost.core

public val kompostLogger: KompostLogger = DefaultLogger

internal interface LoggingControl {
    fun toggleLogging(loggingEnabled: Boolean)
}

public interface KompostLogger {
    public fun log(message: String)

    public companion object {
        public fun toggleLogging(loggingEnabled: Boolean) {
            DefaultLogger.toggleLogging(loggingEnabled)
        }
    }
}

private object DefaultLogger : KompostLogger, LoggingControl {
    private var loggingEnabled: Boolean = false
    override fun log(message: String) {
        if (loggingEnabled) println("KompostDebug: $message")
    }

    override fun toggleLogging(loggingEnabled: Boolean) {
        DefaultLogger.loggingEnabled = loggingEnabled
    }
}
