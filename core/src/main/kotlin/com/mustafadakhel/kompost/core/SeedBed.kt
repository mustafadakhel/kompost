package com.mustafadakhel.kompost.core

/**
 * Represents a [SeedBed] that can produce a crop of type [T].
 *
 * A [SeedBed] is initialized with a [seed] function that produces a crop of type [T].
 * The [seed] function is invoked lazily when the [harvest] function is called for the first time.
 * The result of the [seed] function is stored in the [crop] property and is returned for subsequent calls to the [harvest] function.
 *
 * @constructor Creates a new instance of [SeedBed] with the given [seed] function.
 *
 * @param seed A function that produces a crop of type [T].
 */
internal class SeedBed<T>(
    seed: () -> T,
) {
    /**
     * Represents the crop produced by the [seed] function.
     *
     * The crop is produced lazily when the [harvest] function is called for the first time.
     * The result of the [seed] function is stored in this property and is returned for subsequent calls to the [harvest] function.
     */
    private val crop: Crop<T> by lazy { Crop(seed) }

    /**
     * Harvests the crop from the [SeedBed].
     *
     * If the crop has not been produced yet, it invokes the [seed] function, stores the result in the [crop] property, and returns it.
     * If the crop has already been produced, it returns the stored crop.
     *
     * @return The harvested crop of type [T].
     */
    fun harvest() = crop.harvest()
}
