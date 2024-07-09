package com.mustafadakhel.kompost.sample.di

import com.mustafadakhel.kompost.android.application.ApplicationFarm
import com.mustafadakhel.kompost.lifecycle.activity.createActivitiesFarm

fun ApplicationFarm.activities() = createActivitiesFarm {
    viewModels()
}
