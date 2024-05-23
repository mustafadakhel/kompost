package com.dakhel.kompost

import kotlin.reflect.KClass

/**
 * A constant that holds the delimiter for the tag in the [ProduceKey] value.
 */
private const val TagDelimiter = ":tag="

/**
 * A value class that represents a key for a produce.
 *
 * The [ProduceKey] value is a string that represents a key for a produce.
 * It contains the class name and an optional tag, separated by a delimiter (]TagDelimiter]).
 * The class name and tag are used to uniquely identify a produce in the [Producer].
 *
 * @property value The value of the produce key.
 * @constructor Creates a new instance of [ProduceKey].
 */
@JvmInline
value class ProduceKey private constructor(val value: String) {

    /**
     * Returns the class name from the [ProduceKey] value.
     *
     * The [ProduceKey] value is a string that represents a key for a produce.
     * It contains the class name and an optional tag, separated by a delimiter (]TagDelimiter]).
     * This function retrieves the class name by taking the substring before the [TagDelimiter].
     *
     * @return The class name as a String.
     */
    fun getClassName(): String {
        return value.substringBefore(TagDelimiter)
    }

    /**
     * Returns the tag from the [ProduceKey] value.
     *
     * The [ProduceKey] value is a string that represents a key for a produce.
     * It contains the class name and an optional tag, separated by a delimiter (]TagDelimiter]).
     * This function retrieves the tag by taking the substring after the [TagDelimiter].
     * If no tag is present, an empty string is returned.
     *
     * @return The tag as a String, or an empty string if no tag is present.
     */
    fun getTag(): String {
        return value.substringAfter(TagDelimiter, "")
    }

    /**
     * Creates a new instance of [ProduceKey] by concatenating the class name and the tag (if present) with the [TagDelimiter].
     *
     * @param kClass The class of the produce.
     * @param tag The optional tag of the produce.
     */
    constructor(
        kClass: KClass<*>,
        tag: String? = null
    ) : this("${kClass.produceName}${if (tag != null) "$TagDelimiter$tag" else ""}")

    /**
     * Returns a string representation of the [ProduceKey].
     *
     * @return A string representation of the [ProduceKey].
     */
    override fun toString(): String {
        val tagName = if (getTag().isNotEmpty()) " with tag '${getTag()}'" else ""
        val className = getClassName()
        return "$className$tagName"
    }
}

/**
 * Returns a string representation of the [ProduceKey].
 *
 * The string representation includes the class name and, if present, the tag.
 * The class name and tag are separated by the [TagDelimiter].
 *
 * @return A string representation of the [ProduceKey].
 */
val KClass<*>.produceName: String
    get() = qualifiedName ?: java.name
