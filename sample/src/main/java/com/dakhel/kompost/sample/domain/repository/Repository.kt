package com.dakhel.kompost.sample.domain.repository

import com.dakhel.kompost.sample.domain.model.Model

interface Repository {
    suspend fun getData(): List<Model>
}
