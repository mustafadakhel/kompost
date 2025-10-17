package com.mustafadakhel.kompost.core

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
 * @function getAllKeys This function returns all [ProduceKey]s registered in this [Producer] (excluding parent).
 */
public interface Producer {
    public val id: String
    public val parent: Producer?
    public fun <S> produce(key: ProduceKey, produce: () -> S)
    public fun <S> supply(key: ProduceKey): S
    public fun contains(key: ProduceKey): Boolean
    public fun destroy(key: ProduceKey)
    public fun destroyAllCrops()
    public fun getAllKeys(): Set<ProduceKey>
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
public inline fun <reified S> Producer.produce(
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
public inline fun <reified S> Producer.supply(tag: String? = null): S {
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
public fun <S> Producer.singleton(
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
public inline fun <reified S> Producer.singleton(
    tag: String? = null,
    dependency: S
) {
    val key = ProduceKey(S::class, tag = tag)
    singleton(key, dependency)
}

/**
 * Supplies a type of produce identified by the given [ProduceKey], or returns null if not found.
 *
 * This is a safe variant of [supply] that returns null instead of throwing [NoSuchSeedException]
 * when the dependency is not found. Other exceptions (like [CircularDependencyException] or
 * [CannotCastHarvestedSeedException]) are still thrown.
 *
 * @param S The type of the produce to supply.
 * @param key The [ProduceKey] of the produce to supply.
 * @return The supplied produce of type [S], or null if not found.
 * @throws CircularDependencyException If a circular dependency is detected.
 * @throws CannotCastHarvestedSeedException If the harvested produce cannot be cast to type [S].
 */
public fun <S> Producer.supplyOrNull(key: ProduceKey): S? {
    return try {
        supply(key)
    } catch (e: NoSuchSeedException) {
        null
    }
}

/**
 * Supplies a type of produce, or returns null if not found.
 *
 * This is a safe variant of [supply] that returns null instead of throwing [NoSuchSeedException]
 * when the dependency is not found.
 *
 * @param S The type of the produce to supply.
 * @param tag An optional tag to identify the produce. Defaults to null.
 * @return The supplied produce of type [S], or null if not found.
 */
public inline fun <reified S> Producer.supplyOrNull(tag: String? = null): S? {
    val key = ProduceKey(S::class, tag = tag)
    return supplyOrNull(key)
}

/**
 * Supplies a type of produce identified by the given [ProduceKey], or returns a default value if not found.
 *
 * @param S The type of the produce to supply.
 * @param key The [ProduceKey] of the produce to supply.
 * @param defaultValue The default value to return if the dependency is not found.
 * @return The supplied produce of type [S], or [defaultValue] if not found.
 */
public fun <S> Producer.supplyOrDefault(key: ProduceKey, defaultValue: S): S {
    return supplyOrNull(key) ?: defaultValue
}

/**
 * Supplies a type of produce, or returns a default value if not found.
 *
 * @param S The type of the produce to supply.
 * @param tag An optional tag to identify the produce. Defaults to null.
 * @param defaultValue The default value to return if the dependency is not found.
 * @return The supplied produce of type [S], or [defaultValue] if not found.
 */
public inline fun <reified S> Producer.supplyOrDefault(tag: String? = null, defaultValue: S): S {
    val key = ProduceKey(S::class, tag = tag)
    return supplyOrDefault(key, defaultValue)
}

/**
 * Supplies a type of produce identified by the given [ProduceKey], or computes a fallback value if not found.
 *
 * @param S The type of the produce to supply.
 * @param key The [ProduceKey] of the produce to supply.
 * @param fallback A lambda that computes the fallback value if the dependency is not found.
 * @return The supplied produce of type [S], or the result of [fallback] if not found.
 */
public inline fun <S> Producer.supplyOrElse(key: ProduceKey, fallback: () -> S): S {
    return supplyOrNull(key) ?: fallback()
}

/**
 * Supplies a type of produce, or computes a fallback value if not found.
 *
 * This function allows for lazy computation of the fallback value, which is only
 * executed if the dependency is not found. This is useful for expensive operations
 * or when the fallback requires additional context.
 *
 * Example:
 * ```kotlin
 * val config = farm.supplyOrElse<Config> {
 *     loadDefaultConfig()
 * }
 * ```
 *
 * @param S The type of the produce to supply.
 * @param tag An optional tag to identify the produce. Defaults to null.
 * @param fallback A lambda that computes the fallback value if the dependency is not found.
 * @return The supplied produce of type [S], or the result of [fallback] if not found.
 */
public inline fun <reified S> Producer.supplyOrElse(tag: String? = null, noinline fallback: () -> S): S {
    val key = ProduceKey(S::class, tag = tag)
    return supplyOrElse(key, fallback)
}

/**
 * Checks if this [Producer] contains a dependency of type [S] with an optional tag.
 *
 * This is a convenient reified version of [Producer.contains] that constructs the [ProduceKey]
 * from the type parameter and optional tag.
 *
 * @param S The type of the produce to check for.
 * @param tag An optional tag to identify the produce. Defaults to null.
 * @return True if the dependency exists in this producer, false otherwise.
 */
public inline fun <reified S> Producer.contains(tag: String? = null): Boolean {
    val key = ProduceKey(S::class, tag = tag)
    return contains(key)
}

/**
 * Returns all [ProduceKey]s registered in this [Producer] and all parent producers.
 *
 * This recursively collects keys from this producer and all parent producers up the hierarchy.
 * Useful for debugging and understanding the full dependency graph available from this producer.
 *
 * @return A Set of all [ProduceKey]s available from this producer including parent keys.
 */
public fun Producer.getAllKeysIncludingParents(): Set<ProduceKey> {
    val keys = mutableSetOf<ProduceKey>()
    keys.addAll(getAllKeys())
    parent?.let { keys.addAll(it.getAllKeysIncludingParents()) }
    return keys
}

/**
 * Returns a map representing the dependency graph structure.
 *
 * The map contains the producer ID as key and the set of [ProduceKey]s registered in that producer as value.
 * This includes all producers in the hierarchy (this producer and all parents).
 * Useful for debugging and visualizing the dependency injection structure.
 *
 * @return A Map of producer IDs to their registered [ProduceKey]s.
 */
public fun Producer.getDependencyGraph(): Map<String, Set<ProduceKey>> {
    val graph = mutableMapOf<String, Set<ProduceKey>>()
    graph[id] = getAllKeys()
    parent?.let { graph.putAll(it.getDependencyGraph()) }
    return graph
}
