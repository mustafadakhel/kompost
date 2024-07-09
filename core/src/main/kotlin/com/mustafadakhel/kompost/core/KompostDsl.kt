package com.mustafadakhel.kompost.core

@DslMarker
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION
)
internal annotation class KompostDsl
