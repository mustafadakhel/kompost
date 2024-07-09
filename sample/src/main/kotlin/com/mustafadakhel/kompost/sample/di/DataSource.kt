package com.mustafadakhel.kompost.sample.di

import com.mustafadakhel.kompost.android.application.ApplicationFarm
import com.mustafadakhel.kompost.core.produce
import com.mustafadakhel.kompost.sample.data.source.contract.DataSource
import com.mustafadakhel.kompost.sample.data.source.local.LocalDataSource
import com.mustafadakhel.kompost.sample.data.source.remote.RemoteDataSource
import com.mustafadakhel.kompost.core.supply


fun ApplicationFarm.dataSources() {
    produce<DataSource>("local") { LocalDataSource(supply()) }
    produce<DataSource>("remote") { RemoteDataSource() }
}
