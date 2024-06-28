package com.dakhel.kompost

/**
 * Represents a [Producer] interface in the context of a farming system.
 *
 * A [Producer] is an entity that can produce and supply various types of produce, each associated with a unique [ProduceKey].
 * The [Producer] provides methods to produce a new type of produce, supply an existing type of produce, check if a type of produce exists, and destroy a type of produce or all types of produce.
 * Each [Producer] has a unique identifier and can have a parent [Producer] from which it can retrieve produce if it does not contain the produce itself.
 *
 * @property id The unique identifier of the [Producer].
 * @property parent The parent [Producer] from which to retrieve produce if it is not available in this [Producer].
 * @function produce This function takes a [ProduceKey] and a function that produces an item of type [S]. It is used to produce a new type of produce.
 * @function supply This function takes a [ProduceKey] and returns an item of type [S]. It is used to supply an existing type of produce.
 * @function contains This function takes a [ProduceKey] and returns a Boolean indicating whether the [Producer] contains the type of produce associated with the [ProduceKey].
 * @function destroy This function takes a [ProduceKey] and destroys the type of produce associated with the [ProduceKey].
 * @function destroyAllCrops This function destroys all types of produce in the [Producer].
 */
interface Producer {
    val id: String
    val parent: Producer?
    fun <S> produce(key: ProduceKey, produce: () -> S)
    fun <S> supply(key: ProduceKey): S
    fun contains(key: ProduceKey): Boolean
    fun destroy(key: ProduceKey)
    fun destroyAllCrops()
}

/**
 * An inline function that allows a [Producer] to produce a new type of produce.
 *
 * This function is generic and can be used to produce any type of produce, as specified by the type parameter [S].
 * The type of produce to be produced is identified by a [ProduceKey], which is created using the class of [S] and an optional tag.
 * The actual produce is created by invoking the [produce] function parameter.
 *
 * @receiver The [Producer] instance that will produce the new type of produce.
 * @param S The type of the produce to be produced.
 * @param tag An optional tag that can be used to further identify the type of produce. Defaults to [null].
 * @param produce A function that produces an item of type [S]. This function is invoked to create the actual produce.
 */
@KompostDsl
inline fun <reified S> Producer.produce(
    tag: String? = null,
    noinline produce: () -> S
) {
    val key = ProduceKey(S::class, tag = tag)
    produce(key, produce)
}

/**
 * An inline function that allows a [Producer] to supply a type of produce.
 *
 * This function is generic and can be used to supply any type of produce, as specified by the type parameter [S].
 * The type of produce to be supplied is identified by a [ProduceKey], which is created using the class of [S] and an optional tag.
 *
 * @receiver The [Producer] instance that will supply the produce.
 * @param S The type of the produce to be supplied.
 * @param tag An optional tag that can be used to further identify the type of produce. Defaults to [null].
 * @return The supplied produce of type [S].
 */
inline fun <reified S> Producer.supply(tag: String? = null): S {
    val key = ProduceKey(S::class, tag = tag)
    return supply(key)
}

/**
 * Produces a singleton and adds it to the [Producer].
 *
 * This function is generic and can be used to produce any type of produce, as specified by the type parameter [S].
 * @param S The type of the produce to be produced.
 * @param key The [ProduceKey] under which to add the new produce.
 * @param dependency The instance of the produce to be added.
 */
fun <S> Producer.singleton(
    key: ProduceKey,
    dependency: S
) {
    produce(key) { dependency }
}

/**
 * Produces a singleton and adds it to the [Producer].
 *
 * This function is generic and can be used to produce any type of produce, as specified by the type parameter [S].
 * @param S The type of the produce to be produced.
 * @param tag An optional tag that can be used to further identify the type of produce. Defaults to [null].
 * @param dependency The instance of the produce to be added.
 */
inline fun <reified S> Producer.singleton(
    tag: String? = null,
    dependency: S
) {
    val key = ProduceKey(S::class, tag = tag)
    singleton(key, dependency)
}
