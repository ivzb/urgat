package com.ivzb.urgat.data.base

abstract class BaseGetUseCase<T>(
    private val repository: BaseRepository<T>
) {

    suspend operator fun invoke(id: String): T? {
        return repository.get(id)
    }

}