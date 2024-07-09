package com.mustafadakhel.kompost.sample.di

import com.mustafadakhel.kompost.android.application.ApplicationFarm
import com.mustafadakhel.kompost.sample.data.db.Database
import com.mustafadakhel.kompost.core.singleton

fun ApplicationFarm.misc() {
    singleton(dependency = Database())
}
