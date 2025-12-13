package com.ivzb.urgat.data.resources

import android.content.Context
import androidx.annotation.IntegerRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourcesProvider @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    fun getInteger(@IntegerRes integerResId: Int): Int {
        return context.resources.getInteger(integerResId)
    }
}
