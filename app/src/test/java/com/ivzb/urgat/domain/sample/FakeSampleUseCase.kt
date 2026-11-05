package com.ivzb.urgat.domain.sample

class FakeSampleUseCase : SampleUseCase {

    var invoked: Boolean = false

    override suspend fun invoke() {
        invoked = true
    }
}
