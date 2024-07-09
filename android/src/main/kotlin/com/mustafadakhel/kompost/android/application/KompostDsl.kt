package com.mustafadakhel.kompost.android.application

@DslMarker
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.TYPE,
    AnnotationTarget.FUNCTION
)
internal annotation class KompostDsl
