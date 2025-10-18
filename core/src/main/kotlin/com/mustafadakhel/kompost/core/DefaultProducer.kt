@file:Suppress("unused")

package com.mustafadakhel.kompost.core

/**
 * Returns a [Producer] instance from the parent [Producer] if it contains the given [ProduceKey], or null otherwise.
 *
 * This function checks if the parent [Producer] contains a produce with the given [ProduceKey].
 * If it does, it supplies the produce and returns it.
 * If it does not, it returns null.
 *
 * @param parent The parent [Producer] from which to retrieve the produce.
 * @param key The [ProduceKey] of the produce to retrieve.
 * @return The [Producer] instance if found, or null otherwise.
 */
public fun <T : Producer> producerOrNull(parent: Producer, key: ProduceKey): T? {
    kompostLogger.log("Checking for $key farm in parent: $parent")
    return if (parent.contains(key)) {
        kompostLogger.log("Found $key farm in parent: $parent")
        parent.supply(key)
    } else {
        kompostLogger.log("Could not find $key farm in parent: $parent")
        null
    }
}

/**
 * Represents a [DefaultProducer] which is a specialized type of [Producer].
 *
 * A [DefaultProducer] is a [Producer] that can contain multiple [SeedBed]s, each associated with a unique [ProduceKey].
 * Each [SeedBed] can produce a specific type of produce, which can be any object or data type.
 * The [DefaultProducer] can supply the produce associated with a given [ProduceKey], if it is available.
 * If the produce is not available in the [DefaultProducer], it can optionally retrieve it from a parent [Producer].
 * This allows for a hierarchical structure of [Producer]s, where a [DefaultProducer] can delegate the supply of a produce to its parent if it does not contain the produce itself.
 *
 * The [DefaultProducer] also provides methods to add new [SeedBed]s, check if a [SeedBed] for a specific [ProduceKey] exists, and remove [SeedBed]s.
 * It also provides a method to remove all [SeedBed]s, effectively clearing the [DefaultProducer].
 *
 * @property id The unique identifier of the [DefaultProducer]. This is used to differentiate between different [DefaultProducer]s.
 * @property parent The parent [Producer] from which to retrieve produce if it is not available in the [DefaultProducer]. This can be another [DefaultProducer] or any other type of [Producer].
 * @constructor Creates a new instance of [DefaultProducer].
 */
@KompostDsl
public class DefaultProducer(override val id: String, override val parent: Producer? = null) :
    Producer {
    private val seedBeds = java.util.concurrent.ConcurrentHashMap<ProduceKey, SeedBed<*>>()

    internal companion object {
        private val dependencyStack = ThreadLocal.withInitial { DependencyTracker() }
        private const val MAX_DEPENDENCY_DEPTH = 50

        @JvmStatic
        internal fun clearDependencyTracker() {
            dependencyStack.get().clear()
        }
    }

    private class DependencyTracker {
        private val stack = mutableListOf<ProduceKey>()
        private val seen = mutableSetOf<ProduceKey>()

        fun push(key: ProduceKey) {
            stack.add(key)
            seen.add(key)
        }

        fun pop(key: ProduceKey) {
            stack.removeAt(stack.size - 1)
            seen.remove(key)
        }

        fun contains(key: ProduceKey): Boolean = seen.contains(key)

        fun getChain(): List<ProduceKey> = stack.toList()

        fun depth(): Int = stack.size

        fun clear() {
            stack.clear()
            seen.clear()
        }
    }

    /**
     * Produces a new type of produce and adds it to the [DefaultProducer].
     *
     * This function creates a new [SeedBed] with the given [produce] function and adds it to the [DefaultProducer] under the given [ProduceKey].
     * If a [SeedBed] for the given [ProduceKey] already exists in the [DefaultProducer], it throws a [DuplicateProduceException].
     *
     * @param S The type of the produce to be produced.
     * @param key The [ProduceKey] under which to add the new produce.
     * @param produce A function that produces an item of type [S]. This function is used to create the actual produce.
     * @throws DuplicateProduceException If a [SeedBed] for the given [ProduceKey] already exists in the [DefaultProducer].
     * @throws IllegalArgumentException If the [ProduceKey] value is empty or blank.
     */
    override fun <S> produce(
        key: ProduceKey,
        produce: () -> S
    ) {
        // Input validation
        require(key.value.isNotBlank()) {
            "ProduceKey value cannot be empty or blank. Received: '${key.value}'"
        }

        kompostLogger.log("Producing $key in farm: $this")
        val seedBed = SeedBed(produce)
        if (seedBeds.containsKey(key)) {
            kompostLogger.log("Duplicate produce found for $key in farm: $this")
            throw DuplicateProduceException(key)
        }
        kompostLogger.log("Produced $key in farm: $this")
        seedBeds[key] = seedBed
    }

    /**
     * Checks if the [DefaultProducer] contains a [SeedBed] for the given [ProduceKey].
     *
     * This function checks if the [DefaultProducer] contains a [SeedBed] for the given [ProduceKey] by checking if the [seedBeds] map contains the [ProduceKey].
     *
     * @param key The [ProduceKey] of the [SeedBed] to check.
     * @return A Boolean indicating whether the [DefaultProducer] contains a [SeedBed] for the given [ProduceKey].
     */
    override fun contains(key: ProduceKey): Boolean {
        kompostLogger.log("Checking for $key in farm: $this")
        val found = seedBeds.containsKey(key)
        kompostLogger.log("Found $key in farm: $this: $found")
        return found
    }

    /**
     * Supplies a type of produce identified by the given [ProduceKey].
     *
     * This function retrieves the [SeedBed] associated with the given [ProduceKey] from the [seedBeds] map.
     * If the [SeedBed] is found, it harvests the produce and returns it.
     * If the [SeedBed] is not found, it tries to supply the produce from the parent [Producer].
     * If the parent [Producer] cannot supply the produce, it throws a [NoSuchSeedException].
     * If the harvested produce cannot be cast to the expected type [S], it throws a [CannotCastHarvestedSeedException].
     *
     * @param S The type of the produce to be supply.
     * @param key The [ProduceKey] of the produce to supply.
     * @return The supplied produce of type [S].
     * @throws NoSuchSeedException If the [SeedBed] for the given [ProduceKey] is not found and the parent [Producer] cannot supply the produce.
     * @throws CannotCastHarvestedSeedException If the harvested produce cannot be cast to the expected type [S].
     */
    @Suppress("UNCHECKED_CAST")
    override fun <S> supply(key: ProduceKey): S {
        kompostLogger.log("Supplying $key from farm: $this")
        val bed = seedBeds[key]
        kompostLogger.log("Found $key in farm: $this: $bed")

        if (bed == null) {
            // If we don't have the seed bed, delegate to parent without tracking
            kompostLogger.log("Supplying $key from parent in farm: $this")
            val produceFromParent: S? = parent?.supply(key)
            kompostLogger.log("Supplied $key from parent in farm: $this: $produceFromParent")
            return produceFromParent ?: throw NoSuchSeedException(key)
        }

        // We have the seed bed, so track this dependency before harvesting
        val tracker = dependencyStack.get()

        // Check for circular dependencies
        if (tracker.contains(key)) {
            val chain = tracker.getChain() + key
            throw CircularDependencyException(chain.toSet())
        }

        // Check dependency depth
        if (tracker.depth() >= MAX_DEPENDENCY_DEPTH) {
            val chain = tracker.getChain() + key
            throw CircularDependencyException(
                chain.toSet(),
                "Maximum dependency depth of $MAX_DEPENDENCY_DEPTH exceeded. This might indicate a circular dependency."
            )
        }

        // Track this dependency while harvesting
        tracker.push(key)
        try {
            val harvestedCrop = bed.harvest()
            kompostLogger.log("Harvested $key in farm: $this: $harvestedCrop")
            return harvestedCrop as? S ?: throw CannotCastHarvestedSeedException(key, harvestedCrop)
        } finally {
            // Always pop the key from the tracker, even if an exception occurred
            tracker.pop(key)
        }
    }

    /**
     * Removes the [SeedBed] associated with the given [ProduceKey] from the [DefaultProducer].
     *
     * This function removes the [SeedBed] associated with the given [ProduceKey] from the [seedBeds] map.
     * If the [SeedBed] is not found, this operation has no effect.
     *
     * @param key The [ProduceKey] of the [SeedBed] to remove.
     */
    override fun destroy(key: ProduceKey) {
        kompostLogger.log("Destroying $key in farm: $this")
        seedBeds.remove(key)
    }

    /**
     * Removes all [SeedBed]s from the [DefaultProducer].
     *
     * This function clears the [seedBeds] map, effectively removing all [SeedBed]s from the [DefaultProducer].
     * After this operation, the [DefaultProducer] will not contain any [SeedBed]s, and will not be able to supply any produce until new [SeedBed]s are added.
     */
    override fun destroyAllCrops() {
        kompostLogger.log("Destroying all crops in farm: $this")
        seedBeds.clear()
    }

    /**
     * Returns all [ProduceKey]s registered in this [DefaultProducer].
     *
     * This function retrieves all the keys from the [seedBeds] map.
     * Note that this only returns keys from this producer, not from parent producers.
     *
     * @return A Set of all [ProduceKey]s registered in this [DefaultProducer].
     */
    override fun getAllKeys(): Set<ProduceKey> {
        kompostLogger.log("Getting all keys from farm: $this")
        return seedBeds.keys.toSet()
    }
}

/**
 * Base exception class for all Kompost-specific exceptions.
 *
 * This abstract class serves as the parent for all exceptions thrown by the Kompost library.
 * It allows consumers to catch all Kompost-related exceptions with a single catch block,
 * making error handling more convenient and providing semantic grouping of related errors.
 *
 * Example usage:
 * ```kotlin
 * try {
 *     farm.supply<Dependency>()
 * } catch (e: KompostException) {
 *     // Handle any Kompost-related error
 * }
 * ```
 *
 * @constructor Creates a new instance of [KompostException] with the given message and optional cause.
 */
public abstract class KompostException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Represents an exception that is thrown when a dependency for a given [ProduceKey] is not found.
 *
 * This exception is thrown when a [Producer] tries to supply a produce for a given [ProduceKey], but the [Producer] does not contain a [SeedBed] for that [ProduceKey].
 * The [ProduceKey] of the missing dependency is stored in the [key] property.
 * The message of the exception provides information about the missing dependency and suggests to check the dependency injection setup.
 *
 * @property key The [ProduceKey] of the missing dependency.
 * @constructor Creates a new instance of [NoSuchSeedException].
 */
public class NoSuchSeedException(
    private val key: ProduceKey
) : KompostException(
    message = "Dependency for $key not found. " +
            "Make sure you have produced this dependency properly in your DI setup."
)

/**
 * Represents an exception that is thrown when a harvested produce cannot be cast to the expected type.
 *
 * This exception is thrown when a [Producer] tries to supply a produce for a given [ProduceKey], but the harvested produce cannot be cast to the expected type [S].
 * The [ProduceKey] of the produce and the harvested produce itself are stored in the [key] and [harvestedCrop] properties respectively.
 * The message of the exception provides information about the type mismatch and suggests to check the type definitions and producers.
 *
 * @property key The [ProduceKey] of the produce that could not be cast to the expected type.
 * @property harvestedCrop The harvested produce that could not be cast to the expected type.
 * @constructor Creates a new instance of [CannotCastHarvestedSeedException].
 */
public class CannotCastHarvestedSeedException(
    private val key: ProduceKey,
    private val harvestedCrop: Any?
) : KompostException(
    message = run {
        val harvestedCropClassName = harvestedCrop?.let {
            it::class.qualifiedName ?: it::class.java.name
        } ?: "unknown"
        "Dependency of type '$harvestedCropClassName' was found for '$key', " +
                "but could not be cast to the expected type. Check your type definitions and producers."
    }
)

/**
 * Represents an exception that is thrown when a duplicate produce is being produced in the [DefaultProducer].
 *
 * This exception is thrown when a [Producer] tries to produce a new produce for a given [ProduceKey], but a [SeedBed] for that [ProduceKey] already exists in the [DefaultProducer].
 * The [ProduceKey] of the duplicate produce is stored in the [key] property.
 * The message of the exception provides information about the duplicate produce and suggests to ensure that the same dependency is not being produced multiple times.
 *
 * @property key The [ProduceKey] of the duplicate produce.
 * @constructor Creates a new instance of [DuplicateProduceException].
 */
public class DuplicateProduceException(
    private val key: ProduceKey,
) : KompostException(
    message = "An instance for $key has already been produced in this farm. " +
            "Make sure you are not producing the same dependency multiple times."
)

/**
 * Represents an exception that is thrown when a circular dependency is detected.
 *
 * This exception is thrown when a [Producer] detects that a dependency is trying to supply itself,
 * either directly or indirectly through a chain of dependencies. This prevents infinite loops
 * and stack overflow errors.
 *
 * The dependency chain is stored in the [dependencyChain] property, which shows the path of
 * dependencies that led to the circular reference.
 *
 * Example:
 * ```
 * produce<A> { A(supply<B>()) }
 * produce<B> { B(supply<A>()) }
 * // Throws: CircularDependencyException with chain: [A, B, A]
 * ```
 *
 * @property dependencyChain The chain of dependencies that formed the circular reference.
 * @property customMessage Optional custom error message to provide additional context.
 * @constructor Creates a new instance of [CircularDependencyException].
 */
public class CircularDependencyException(
    private val dependencyChain: Set<ProduceKey>,
    private val customMessage: String? = null
) : KompostException(
    message = run {
        val chain = dependencyChain.joinToString(" -> ")
        val baseMessage = "Circular dependency detected: $chain"
        val explanation = customMessage ?:
            "A dependency is trying to supply itself, either directly or through a chain of other dependencies. " +
            "Please review your dependency injection setup and break the circular reference."
        "$baseMessage\n$explanation"
    }
)
