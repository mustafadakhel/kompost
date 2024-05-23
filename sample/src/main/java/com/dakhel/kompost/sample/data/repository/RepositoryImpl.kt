package com.dakhel.kompost.sample.data.repository

import com.dakhel.kompost.sample.data.source.contract.DataSource
import com.dakhel.kompost.sample.domain.model.Model
import com.dakhel.kompost.sample.domain.repository.Repository

class RepositoryImpl(
    private val localDataSource: DataSource,
    private val remoteDataSource: DataSource
) : Repository {
    override suspend fun getData(): List<Model> {
        return listOf(Model(1, " Data"))
    }
}
