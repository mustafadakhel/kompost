package com.dakhel.kompost

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
