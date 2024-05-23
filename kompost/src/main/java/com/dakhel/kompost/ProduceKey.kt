package com.dakhel.kompost

import kotlin.reflect.KClass

private const val TagDelimiter = ":tag="

@JvmInline
value class ProduceKey private constructor(val value: String) {

    fun getClassName(): String {
        return value.substringBefore(TagDelimiter)
    }

    fun getTag(): String {
        return value.substringAfter(TagDelimiter, "")
    }

    constructor(
        kClass: KClass<*>,
        tag: String? = null
    ) : this("${kClass.produceName}${if (tag != null) "$TagDelimiter$tag" else ""}")

    override fun toString(): String {
        val tagName = if (getTag().isNotEmpty()) " with tag '${getTag()}'" else ""
        val className = getClassName()
        return "$className$tagName"
    }
}

val KClass<*>.produceName: String
    get() = qualifiedName ?: java.name