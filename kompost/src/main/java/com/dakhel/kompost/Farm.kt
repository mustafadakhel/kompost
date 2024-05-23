@file:Suppress("unused")

package com.dakhel.kompost

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
fun <T : Producer> farmOrNull(parent: Producer, key: ProduceKey): T? {
    return if (parent.contains(key)) {
        parent.supply(key)
    } else null
}

/**
 * Represents a [Farm] which is a specialized type of [Producer].
 *
 * A [Farm] is a [Producer] that can contain multiple [SeedBed]s, each associated with a unique [ProduceKey].
 * Each [SeedBed] can produce a specific type of produce, which can be any object or data type.
 * The [Farm] can supply the produce associated with a given [ProduceKey], if it is available.
 * If the produce is not available in the [Farm], it can optionally retrieve it from a parent [Producer].
 * This allows for a hierarchical structure of [Producer]s, where a [Farm] can delegate the supply of a produce to its parent if it does not contain the produce itself.
 *
 * The [Farm] also provides methods to add new [SeedBed]s, check if a [SeedBed] for a specific [ProduceKey] exists, and remove [SeedBed]s.
 * It also provides a method to remove all [SeedBed]s, effectively clearing the [Farm].
 *
 * @property id The unique identifier of the [Farm]. This is used to differentiate between different [Farm]s.
 * @property parent The parent [Producer] from which to retrieve produce if it is not available in the [Farm]. This can be another [Farm] or any other type of [Producer].
 * @constructor Creates a new instance of [Farm].
 */
class Farm(override val id: String, override val parent: Producer? = null) : Producer {
    private val seedBeds = mutableMapOf<String, SeedBed<*>>()

    /**
     * Produces a new type of produce and adds it to the [Farm].
     *
     * This function creates a new [SeedBed] with the given [produce] function and adds it to the [Farm] under the given [ProduceKey].
     * If a [SeedBed] for the given [ProduceKey] already exists in the [Farm], it throws a [DuplicateProduceException].
     *
     * @param S The type of the produce to be produced.
     * @param key The [ProduceKey] under which to add the new produce.
     * @param produce A function that produces an item of type [S]. This function is used to create the actual produce.
     * @throws DuplicateProduceException If a [SeedBed] for the given [ProduceKey] already exists in the [Farm].
     */
    override fun <S> produce(
        key: ProduceKey,
        produce: () -> S
    ) {
        val seedBed = SeedBed(produce)
        if (seedBeds.containsKey(key.value)) throw DuplicateProduceException(key)
        seedBeds[key.value] = seedBed
    }

    /**
     * Checks if the [Farm] contains a [SeedBed] for the given [ProduceKey].
     *
     * This function checks if the [Farm] contains a [SeedBed] for the given [ProduceKey] by checking if the [seedBeds] map contains a key that matches the value of the [ProduceKey].
     *
     * @param key The [ProduceKey] of the [SeedBed] to check.
     * @return A Boolean indicating whether the [Farm] contains a [SeedBed] for the given [ProduceKey].
     */
    override fun contains(key: ProduceKey): Boolean {
        return seedBeds.containsKey(key.value)
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
    override fun <S> supply(key: ProduceKey): S {
        val bed = seedBeds[key.value]
        if (bed == null) {
            val produceFromParent: S? = parent?.supply(key)
            return produceFromParent ?: throw NoSuchSeedException(key)
        }
        val harvestedCrop = bed.harvest()
        return harvestedCrop as? S ?: throw CannotCastHarvestedSeedException(key, harvestedCrop)
    }

    /**
     * Removes the [SeedBed] associated with the given [ProduceKey] from the [Farm].
     *
     * This function removes the [SeedBed] associated with the given [ProduceKey] from the [seedBeds] map.
     * If the [SeedBed] is not found, this operation has no effect.
     *
     * @param key The [ProduceKey] of the [SeedBed] to remove.
     */
    override fun destroy(key: ProduceKey) {
        seedBeds.remove(key.value)
    }

    /**
     * Removes all [SeedBed]s from the [Farm].
     *
     * This function clears the [seedBeds] map, effectively removing all [SeedBed]s from the [Farm].
     * After this operation, the [Farm] will not contain any [SeedBed]s, and will not be able to supply any produce until new [SeedBed]s are added.
     */
    override fun destroyAllCrops() {
        seedBeds.clear()
    }
}

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
class NoSuchSeedException(
    private val key: ProduceKey
) : Exception() {
    override val message: String
        get() {
            return "Dependency for '$key' not found. " +
                    "Make sure you have produced this dependency properly in your DI setup."
        }
}

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
class CannotCastHarvestedSeedException(
    private val key: ProduceKey,
    private val harvestedCrop: Any?
) : Exception() {
    override val message: String
        get() {
            val harvestedCropClassName = harvestedCrop?.let {
                it::class.qualifiedName ?: it::class.java.name
            } ?: "unknown"
            return "Dependency of type '$harvestedCropClassName' was found for '$key', " +
                    "but could not be cast to the expected type. Check your type definitions and producers."
        }
}

/**
 * Represents an exception that is thrown when a duplicate produce is being produced in the [Farm].
 *
 * This exception is thrown when a [Producer] tries to produce a new produce for a given [ProduceKey], but a [SeedBed] for that [ProduceKey] already exists in the [Farm].
 * The [ProduceKey] of the duplicate produce is stored in the [key] property.
 * The message of the exception provides information about the duplicate produce and suggests to ensure that the same dependency is not being produced multiple times.
 *
 * @property key The [ProduceKey] of the duplicate produce.
 * @constructor Creates a new instance of [DuplicateProduceException].
 */
class DuplicateProduceException(
    private val key: ProduceKey,
) : Exception() {
    override val message: String
        get() {
            return "An instance for $key has already been produced in this farm. " +
                    "Make sure you are not producing the same dependency multiple times."
        }
}
