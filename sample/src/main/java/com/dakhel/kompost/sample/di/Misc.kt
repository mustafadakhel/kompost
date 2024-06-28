package com.dakhel.kompost.sample.di

import com.dakhel.kompost.application.ApplicationFarm
import com.dakhel.kompost.produce
import com.dakhel.kompost.sample.data.db.Database

fun ApplicationFarm.misc() {
    val database = Database()
    produce(produce = { database })
}
