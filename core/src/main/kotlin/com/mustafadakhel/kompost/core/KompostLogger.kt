package com.mustafadakhel.kompost.core

/**
 * Global logger instance used throughout Kompost.
 * Can be replaced with a custom implementation via [setKompostLogger].
 */
public var kompostLogger: KompostLogger = DefaultLogger
    private set

/**
 * Log levels for Kompost logging system, ordered from least to most severe.
 */
public enum class LogLevel {
    /** Detailed diagnostic information for debugging */
    DEBUG,
    /** General informational messages */
    INFO,
    /** Warning messages for potentially problematic situations */
    WARN,
    /** Error messages for serious problems */
    ERROR
}

/**
 * Sets a custom logger implementation for Kompost.
 * Useful for integrating with application-specific logging frameworks.
 *
 * Example:
 * ```kotlin
 * setKompostLogger(object : KompostLogger {
 *     override fun log(level: LogLevel, message: String, context: Map<String, String>?) {
 *         when (level) {
 *             LogLevel.ERROR -> Log.e("Kompost", message)
 *             LogLevel.WARN -> Log.w("Kompost", message)
 *             else -> Log.d("Kompost", message)
 *         }
 *     }
 * })
 * ```
 *
 * @param logger The custom logger implementation to use
 */
public fun setKompostLogger(logger: KompostLogger) {
    kompostLogger = logger
}

/**
 * Configures the default logger's behavior.
 *
 * @param enabled Whether logging is enabled
 * @param minLevel The minimum log level to output (default: DEBUG)
 */
public fun configureDefaultLogger(
    enabled: Boolean,
    minLevel: LogLevel = LogLevel.DEBUG
) {
    DefaultLogger.configure(enabled, minLevel)
}

/**
 * Interface for Kompost logging.
 * Implement this interface to provide custom logging behavior.
 */
public interface KompostLogger {
    /**
     * Logs a message at the specified level with optional context.
     *
     * @param level The log level
     * @param message The message to log
     * @param context Optional key-value pairs providing additional context
     */
    public fun log(level: LogLevel, message: String, context: Map<String, String>? = null)

    /**
     * Logs a message at DEBUG level (for backward compatibility).
     * Equivalent to calling log(LogLevel.DEBUG, message).
     *
     * @param message The message to log
     */
    public fun log(message: String) {
        log(LogLevel.DEBUG, message, null)
    }

    public companion object {
        /**
         * Enables or disables logging for the default logger.
         * For backward compatibility.
         *
         * @param loggingEnabled Whether logging should be enabled
         */
        public fun toggleLogging(loggingEnabled: Boolean) {
            DefaultLogger.configure(loggingEnabled, DefaultLogger.minLevel)
        }
    }
}

private object DefaultLogger : KompostLogger {
    internal var loggingEnabled: Boolean = false
    internal var minLevel: LogLevel = LogLevel.DEBUG

    fun configure(enabled: Boolean, minLevel: LogLevel) {
        this.loggingEnabled = enabled
        this.minLevel = minLevel
    }

    override fun log(level: LogLevel, message: String, context: Map<String, String>?) {
        if (!loggingEnabled || level.ordinal < minLevel.ordinal) return

        val contextStr = context?.entries?.joinToString(", ") { "${it.key}=${it.value}" }
            ?.let { " [$it]" } ?: ""

        val levelStr = when (level) {
            LogLevel.DEBUG -> "[DEBUG]"
            LogLevel.INFO -> "[INFO]"
            LogLevel.WARN -> "[WARN]"
            LogLevel.ERROR -> "[ERROR]"
        }

        println("Kompost $levelStr: $message$contextStr")
    }
}
