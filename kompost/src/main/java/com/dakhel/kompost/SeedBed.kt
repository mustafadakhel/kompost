package com.dakhel.kompost

class SeedBed<T>(
    seed: () -> T,
) {
    private val crop: Crop<T> by lazy { Crop(seed) }

    fun harvest() = crop.harvest()
}