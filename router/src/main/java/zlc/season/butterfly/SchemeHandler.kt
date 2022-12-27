package zlc.season.butterfly

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import zlc.season.butterfly.internal.ButterflyHelper
import zlc.season.butterfly.internal.key

@OptIn(FlowPreview::class)
class SchemeHandler(
    var request: SchemeRequest,
    val interceptorManager: InterceptorManager = InterceptorManager()
) {
    companion object {
        private const val DEFAULT_GROUP = "butterfly_group"
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

    fun addInterceptor(interceptor: ButterflyInterceptor): SchemeHandler {
        return apply { interceptorManager.addInterceptor(interceptor) }
    }

    fun addInterceptor(interceptor: suspend (SchemeRequest) -> Unit): SchemeHandler {
        return apply {
            interceptorManager.addInterceptor(DefaultButterflyInterceptor(interceptor))
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

    fun flow(): Flow<Unit> {
        val handler = configRequest { copy(needResult = false) }
        return ButterflyCore.dispatchScheme(handler.request, handler.interceptorManager)
            .flatMapConcat {
                if (it.isSuccess) {
                    flowOf(Unit)
                } else {
                    throw it.exceptionOrNull() ?: Throwable()
                }
            }
    }

    fun resultFlow(): Flow<Result<Bundle>> {
        val handler = configRequest { copy(needResult = true) }
        return ButterflyCore.dispatchScheme(handler.request, handler.interceptorManager)
    }

    fun route(
        scope: CoroutineScope = ButterflyHelper.scope,
        onError: (Throwable) -> Unit = {},
        onResult: (Bundle) -> Unit = EMPTY_LAMBDA
    ) {
        if (onResult == EMPTY_LAMBDA) {
            flow().catch { onError(it) }.launchIn(scope)
        } else {
            resultFlow().onEach {
                if (it.isSuccess) {
                    onResult(it.getOrDefault(Bundle()))
                } else {
                    onError(it.exceptionOrNull() ?: Throwable())
                }
            }.launchIn(scope)
        }
    }

    fun getLauncher(): SchemeLauncher {
        val schemeHandler = configRequest { copy(needResult = true) }

        val activity = ButterflyHelper.fragmentActivity
            ?: throw IllegalStateException("No FragmentActivity founded!")

        if (activity.lifecycle.currentState != Lifecycle.State.INITIALIZED) {
            throw IllegalStateException("You must call getLauncher() after activity are created.")
        }

        val key = activity.key()
        var launcher = SchemeLauncherManager.getLauncher(key, request.scheme)
        if (launcher == null) {
            launcher = SchemeLauncher(schemeHandler.request, schemeHandler.interceptorManager)
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

