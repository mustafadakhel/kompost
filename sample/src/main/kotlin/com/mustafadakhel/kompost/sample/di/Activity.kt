package com.mustafadakhel.kompost.sample.di

import com.mustafadakhel.kompost.android.application.ApplicationFarm
import com.mustafadakhel.kompost.android.lifecycle.activity.createActivityScopedFarm

fun ApplicationFarm.activities() = createActivityScopedFarm {
    viewModels()
}
