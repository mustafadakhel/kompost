package com.dakhel.kompost.sample.di

import com.dakhel.kompost.application.ApplicationFarm
import com.dakhel.kompost.produce
import com.dakhel.kompost.sample.data.repository.RepositoryImpl
import com.dakhel.kompost.sample.data.source.contract.DataSource
import com.dakhel.kompost.sample.data.source.local.LocalDataSource
import com.dakhel.kompost.sample.data.source.remote.RemoteDataSource
import com.dakhel.kompost.sample.domain.repository.Repository
import com.dakhel.kompost.supply

fun ApplicationFarm.repositories() {
    produce<Repository> {
        RepositoryImpl(
            supply("local"),
            supply("remote")
        )
    }
}
