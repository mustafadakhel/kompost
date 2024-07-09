package com.mustafadakhel.kompost.sample.data.source.local

import com.mustafadakhel.kompost.sample.data.db.Database
import com.mustafadakhel.kompost.sample.data.source.contract.DataSource

class LocalDataSource(
    private val database: Database
) : DataSource
