package com.mustafadakhel.kompost.android.lifecycle

interface GlobalDependency {
    fun doSomethingGlobal()
}

interface SomeDependency {
    fun doSomething()
}

class AnotherDependency {
    fun doAnotherThing() {}
}

class DependencyWithParams(val param: String)
