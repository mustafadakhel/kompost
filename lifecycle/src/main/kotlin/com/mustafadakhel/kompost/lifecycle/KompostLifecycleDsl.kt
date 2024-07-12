package com.mustafadakhel.kompost.lifecycle

@DslMarker
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION
)
internal annotation class KompostLifecycleDsl
