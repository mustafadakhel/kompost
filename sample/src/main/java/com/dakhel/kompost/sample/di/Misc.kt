package com.dakhel.kompost.sample.di

import com.dakhel.kompost.application.ApplicationFarm
import com.dakhel.kompost.sample.data.db.Database
import com.dakhel.kompost.singleton

fun ApplicationFarm.misc() {
    singleton(dependency = Database())
}
