package com.ivzb.urgat.domain.base

import com.ivzb.urgat.data.base.BaseRepository

abstract class BaseGetUseCase<T>(
    private val repository: BaseRepository<T>
) {

    suspend operator fun invoke(id: String): T? {
        return repository.get(id)
    }

}