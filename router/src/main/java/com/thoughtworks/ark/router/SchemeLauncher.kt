package com.thoughtworks.ark.router

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import com.thoughtworks.ark.router.internal.InternalHelper
import com.thoughtworks.ark.router.internal.logw

class SchemeLauncher(
    val schemeRequest: SchemeRequest,
    val interceptorManager: InterceptorManager
) {
    val flow = MutableSharedFlow<Result<Bundle>>(extraBufferCapacity = 1)

    fun result(): Flow<Result<Bundle>> {
        return flow
    }

    fun launch() {
        val activity = InternalHelper.fragmentActivity
        if (activity == null) {
            "No valid Activity found!".logw()
            return
        }
        RouterCore.dispatchScheme(schemeRequest, interceptorManager).launchIn(activity.lifecycleScope)
    }
}