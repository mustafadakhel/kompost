package com.dakhel.kompost

/**
 * A set of extension functions for the `Producer` interface.
 * These functions are used to produce a constructor with a varying number of parameters.
 * The parameters are supplied from the `Producer`.
 *
 * Each function takes a `constructor` parameter, which is a function that takes a certain number of parameters and returns an instance of `R`.
 * The `constructor` function is called with the supplied parameters to produce the instance of `R`.
 *
 * The functions use inline reified type parameters to infer the types of the parameters and the return type.
 * This allows the functions to work with any types.
 *
 * The functions use the `produce` function to produce the instance of `R`.
 * The `produce` function is called with a lambda that calls the `constructor` function with the supplied parameters.
 *
 * The functions use the `supply` function to supply the parameters for the `constructor` function.
 * The `supply` function is called with no arguments, so it supplies the parameters in the order they are declared.
 *
 * The functions are defined for constructors with up to 20 parameters.
 */

inline fun <reified R> Producer.produceConstructor(
    crossinline constructor: () -> R,
) = produce { constructor() }

inline fun <reified R, reified T1> Producer.produceConstructor(
    crossinline constructor: (T1) -> R,
) = produce { constructor(supply()) }

inline fun <reified R, reified T1, reified T2> Producer.produceConstructor(
    crossinline constructor: (T1, T2) -> R,
) = produce {
    constructor(
        supply(), supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3) -> R,
) = produce {
    constructor(
        supply(), supply(), supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4) -> R,
) = produce {
    constructor(
        supply(), supply(), supply(), supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5) -> R,
) = produce {
    constructor(
        supply(), supply(), supply(), supply(), supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6) -> R,
) = produce {
    constructor(
        supply(), supply(), supply(), supply(), supply(), supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7) -> R,
) = produce {
    constructor(
        supply(), supply(), supply(), supply(), supply(), supply(), supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8) -> R,
) = produce {
    constructor(
        supply(), supply(), supply(), supply(), supply(), supply(), supply(), supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R,
) = produce {
    constructor(
        supply(), supply(), supply(), supply(), supply(), supply(), supply(), supply(), supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20> Producer.produceConstructor(
    crossinline constructor: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R,
) = produce {
    constructor(
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply(),
        supply()
    )
}
