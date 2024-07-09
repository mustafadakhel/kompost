package com.mustafadakhel.kompost.sample.di

import com.mustafadakhel.kompost.android.application.ApplicationFarm
import com.mustafadakhel.kompost.core.produce
import com.mustafadakhel.kompost.sample.data.repository.RepositoryImpl
import com.mustafadakhel.kompost.sample.domain.repository.Repository
import com.mustafadakhel.kompost.core.supply

fun ApplicationFarm.repositories() {
    produce<Repository> {
        RepositoryImpl(
            supply("local"),
            supply("remote")
        )
    }
}
