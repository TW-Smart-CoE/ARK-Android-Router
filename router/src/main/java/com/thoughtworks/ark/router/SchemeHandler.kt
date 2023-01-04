package com.thoughtworks.ark.router

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.thoughtworks.ark.router.internal.InternalHelper.findActivity
import com.thoughtworks.ark.router.internal.InternalHelper.findScope
import com.thoughtworks.ark.router.internal.key
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@OptIn(FlowPreview::class)
class SchemeHandler(
    var request: SchemeRequest,
    val interceptorManager: InterceptorManager = InterceptorManager()
) {
    companion object {
        private const val DEFAULT_GROUP = "router_group"
        private val EMPTY_LAMBDA: (Bundle) -> Unit = {}
    }

    fun params(vararg pair: Pair<String, Any?>): SchemeHandler {
        return apply {
            request.bundle.putAll(bundleOf(*pair))
        }
    }

    fun params(bundle: Bundle): SchemeHandler {
        return apply { request.bundle.putAll(bundle) }
    }

    fun skipGlobalInterceptor(): SchemeHandler {
        return configRequest { copy(enableGlobalInterceptor = false) }
    }

    fun addInterceptor(interceptor: RouterInterceptor): SchemeHandler {
        return apply { interceptorManager.addInterceptor(interceptor) }
    }

    fun addInterceptor(interceptor: suspend (SchemeRequest) -> SchemeRequest): SchemeHandler {
        return apply {
            interceptorManager.addInterceptor(DefaultRouterInterceptor(interceptor))
        }
    }

    fun container(containerViewId: Int): SchemeHandler {
        return configRequest {
            copy(containerViewId = containerViewId)
        }
    }

    fun group(groupName: String = DEFAULT_GROUP): SchemeHandler {
        return configRequest { copy(groupId = groupName) }
    }

    fun clearTop(): SchemeHandler {
        return configRequest { copy(clearTop = true) }
    }

    fun singleTop(): SchemeHandler {
        return configRequest { copy(singleTop = true) }
    }

    fun asRoot(): SchemeHandler {
        return configRequest { copy(isRoot = true) }
    }

    fun disableBackStack(): SchemeHandler {
        return configRequest { copy(enableBackStack = false) }
    }

    fun addFlag(flag: Int): SchemeHandler {
        return configRequest { copy(flags = flags or flag) }
    }

    fun enterAnim(enterAnim: Int = 0): SchemeHandler {
        return configRequest { copy(enterAnim = enterAnim) }
    }

    fun exitAnim(exitAnim: Int = 0): SchemeHandler {
        return configRequest { copy(exitAnim = exitAnim) }
    }

    fun flow(context: Context): Flow<Unit> {
        val handler = configRequest { copy(needResult = false) }
        return RouterCore.dispatchScheme(context, handler.request, handler.interceptorManager)
            .flatMapConcat {
                if (it.isSuccess) {
                    flowOf(Unit)
                } else {
                    throw it.exceptionOrNull() ?: Throwable()
                }
            }
    }

    fun resultFlow(context: Context): Flow<Result<Bundle>> {
        val handler = configRequest { copy(needResult = true) }
        return RouterCore.dispatchScheme(context, handler.request, handler.interceptorManager)
    }

    fun route(
        context: Context,
        onError: (Throwable) -> Unit = {},
        onSuccess: () -> Unit = {},
        onResult: (Bundle) -> Unit = EMPTY_LAMBDA
    ) {
        if (onResult == EMPTY_LAMBDA) {
            flow(context).catch { onError(it) }
                .onCompletion { onSuccess() }
                .launchIn(context.findScope())
        } else {
            resultFlow(context)
                .onEach {
                    if (it.isSuccess) {
                        onResult(it.getOrDefault(Bundle()))
                    } else {
                        onError(it.exceptionOrNull() ?: Throwable())
                    }
                }
                .catch { onError(it) }
                .onCompletion { onSuccess() }
                .launchIn(context.findScope())
        }
    }

    fun getLauncher(context: Context): SchemeLauncher {
        val schemeHandler = configRequest { copy(needResult = true) }

        val activity = context.findActivity()
            ?: throw IllegalStateException("No Activity founded!")

        val key = activity.key()
        var launcher = SchemeLauncherManager.getLauncher(key, request.scheme)
        if (launcher == null) {
            launcher = SchemeLauncher(context, schemeHandler.request, schemeHandler.interceptorManager)
            SchemeLauncherManager.addLauncher(key, launcher)
        }

        return launcher
    }

    private fun configRequest(block: SchemeRequest.() -> SchemeRequest): SchemeHandler {
        return apply {
            request = request.block()
        }
    }
}

