package com.dakhel.kompost.sample.di

import com.dakhel.kompost.application.ApplicationFarm
import com.dakhel.kompost.produce
import com.dakhel.kompost.sample.data.source.contract.DataSource
import com.dakhel.kompost.sample.data.source.local.LocalDataSource
import com.dakhel.kompost.sample.data.source.remote.RemoteDataSource
import com.dakhel.kompost.supply


fun ApplicationFarm.dataSources() {
    produce<DataSource>("local") { LocalDataSource(supply()) }
    produce<DataSource>("remote") { RemoteDataSource() }
}
