package com.mustafadakhel.kompost.android.lifecycle

@DslMarker
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION
)
annotation class KompostLifecycleDsl
