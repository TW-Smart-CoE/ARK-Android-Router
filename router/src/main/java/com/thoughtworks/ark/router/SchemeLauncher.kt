package com.thoughtworks.ark.router

import android.content.Context
import android.os.Bundle
import com.thoughtworks.ark.router.internal.InternalHelper.findScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn

class SchemeLauncher(
    val context: Context,
    val schemeRequest: SchemeRequest,
    val interceptorManager: InterceptorManager
) {
    val flow = MutableSharedFlow<Result<Bundle>>(extraBufferCapacity = 1)

    fun result(): Flow<Result<Bundle>> {
        return flow
    }

    fun launch() {
        RouterCore.dispatchScheme(context, schemeRequest, interceptorManager)
            .launchIn(context.findScope())
    }
}