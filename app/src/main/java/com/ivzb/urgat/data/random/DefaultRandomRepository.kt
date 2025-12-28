package com.ivzb.urgat.data.random

import com.ivzb.urgat.util.randomUUID
import javax.inject.Inject

class DefaultRandomRepository @Inject constructor() : RandomRepository {

    override fun uuid(): String = randomUUID()

}