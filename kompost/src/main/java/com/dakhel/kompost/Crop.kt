package com.dakhel.kompost

/**
 * Represents a `Crop` that can be harvested from a `SeedBed`.
 *
 * A `Crop` is initialized with a `seed` function that produces a crop of type `T`.
 * The `seed` function is invoked when the `harvest` function is called.
 *
 * @property seed A function that produces a crop of type `T`.
 * @constructor Creates a new instance of `Crop` with the given `seed` function.
 */
class Crop<T>(private val seed: () -> T) {
    /**
     * Harvests the crop from the `Crop`.
     *
     * It invokes the `seed` function and returns the result.
     *
     * @return The harvested crop of type `T`.
     */
    fun harvest(): T = seed.invoke()
}
