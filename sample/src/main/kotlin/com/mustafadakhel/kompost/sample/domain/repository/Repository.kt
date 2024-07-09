package com.mustafadakhel.kompost.sample.domain.repository

import com.mustafadakhel.kompost.sample.domain.model.Model

interface Repository {
    suspend fun getData(): List<Model>
}
