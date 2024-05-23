@file:Suppress("unused")

package com.dakhel.kompost

fun <T : Producer> farmOrNull(parent: Producer, key: ProduceKey): T? {
    return if (parent.contains(key)) {
        parent.supply(key)
    } else null
}

class Farm(override val id: String, override val parent: Producer? = null) : Producer {
    private val seedBeds = mutableMapOf<String, SeedBed<*>>()

    override fun <S> produce(
        key: ProduceKey,
        produce: () -> S
    ) {
        val seedBed = SeedBed(produce)
        if (seedBeds.containsKey(key.value)) throw DuplicateProduceException(key)
        seedBeds[key.value] = seedBed
    }

    override fun contains(key: ProduceKey): Boolean {
        return seedBeds.containsKey(key.value)
    }

    override fun <S> supply(key: ProduceKey): S {
        val bed = seedBeds[key.value]
        if (bed == null) {
            val produceFromParent: S? = parent?.supply(key)
            return produceFromParent ?: throw NoSuchSeedException(key)
        }
        val harvestedCrop = bed.harvest()
        return harvestedCrop as? S ?: throw CannotCastHarvestedSeedException(key, harvestedCrop)
    }

    override fun destroy(key: ProduceKey) {
        seedBeds.remove(key.value)
    }

    override fun destroyAllCrops() {
        seedBeds.clear()
    }
}

class NoSuchSeedException(
    private val key: ProduceKey
) : Exception() {
    override val message: String
        get() {
            return "Dependency for '$key' not found. " +
                    "Make sure you have produced this dependency properly in your DI setup."
        }
}

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

class DuplicateProduceException(
    private val key: ProduceKey,
) : Exception() {
    override val message: String
        get() {
            return "An instance for $key has already been produced in this farm. " +
                    "Make sure you are not producing the same dependency multiple times."
        }
}
