package com.dakhel.kompost.lifecycle

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ActivityScenario

inline fun <reified F : Fragment, T> FragmentScenario<F>.withFragmentNullable(
    crossinline block: F.() -> T
): T? {
    var value: T? = null
    var err: Throwable? = null
    onFragment { fragment ->
        try {
            value = block(fragment)
        } catch (t: Throwable) {
            err = t
        }
    }
    err?.let { throw it }
    return value
}

inline fun <reified F : Activity, T> ActivityScenario<F>.withActivityNullable(
    crossinline block: F.() -> T
): T? {
    var value: T? = null
    var err: Throwable? = null
    onActivity { fragment ->
        try {
            value = block(fragment)
        } catch (t: Throwable) {
            err = t
        }
    }
    err?.let { throw it }
    return value
}
