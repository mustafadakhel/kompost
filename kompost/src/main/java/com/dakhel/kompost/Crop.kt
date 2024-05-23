package com.dakhel.kompost

class Crop<T>(private val seed: () -> T) {
    fun harvest(): T = seed.invoke()
}