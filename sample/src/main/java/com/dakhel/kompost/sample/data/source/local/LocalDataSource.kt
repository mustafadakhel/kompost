package com.dakhel.kompost.sample.data.source.local

import com.dakhel.kompost.sample.data.db.Database
import com.dakhel.kompost.sample.data.source.contract.DataSource

class LocalDataSource(
    private val database: Database
) : DataSource
