package com.dakhel.kompost

interface Producer {
    val id: String
    val parent: Producer?
    fun <S> produce(key: ProduceKey, produce: () -> S)
    fun <S> supply(key: ProduceKey): S
    fun contains(key: ProduceKey): Boolean
    fun destroy(key: ProduceKey)
    fun destroyAllCrops()
}

inline fun <reified S> Producer.produce(
    tag: String? = null,
    noinline produce: () -> S
) {
    val key = ProduceKey(S::class, tag = tag)
    produce(key, produce)
}

inline fun <reified S> Producer.supply(tag: String? = null): S {
    val key = ProduceKey(S::class, tag = tag)
    return supply(key)
}

inline fun <reified S> Producer.contains(tag: String? = null): Boolean {
    val key = ProduceKey(S::class, tag = tag)
    return contains(key)
}

internal fun noSeedFoundForKeyExceptionMessage(key: ProduceKey): String {
    val className = key.getClassName()
    val tag = key.getTag()
    return if (tag.isNotEmpty())
        "Seed for $className with tag '$tag' not planted"
    else "Seed for $className not planted"
}
