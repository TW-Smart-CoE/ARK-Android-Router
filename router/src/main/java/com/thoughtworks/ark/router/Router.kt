package com.thoughtworks.ark.router

import androidx.core.os.bundleOf
import com.thoughtworks.ark.router.internal.parseScheme
import com.thoughtworks.ark.router.internal.parseSchemeParams

object Router {
    fun enableLog(flag: Boolean) {
        com.thoughtworks.ark.router.internal.enableLog = flag
    }

    fun scheme(scheme: String): SchemeHandler {
        val realScheme = parseScheme(scheme)
        val request = RouterCore.queryScheme(realScheme).apply {
            val params = parseSchemeParams(scheme)
            bundle.putAll(bundleOf(*params))
        }
        return SchemeHandler(request)
    }

    fun back(vararg result: Pair<String, Any?>): SchemeRequest? {
        return RouterCore.dispatchBack(bundleOf(*result))
    }

    // avoid inline
    val SERVICE_LAMBDA: (String, Class<*>) -> Any = { identity, cls ->
        val real = identity.ifEmpty { cls.simpleName }
        var request = RouterCore.queryService(real)
        if (request.className.isEmpty()) {
            request = request.copy(className = cls.name)
        }
        RouterCore.dispatchService(request)
    }

    inline fun <reified T> service(
        identity: String = "",
        noinline func: (String, Class<*>) -> Any = SERVICE_LAMBDA
    ): T {
        return func(identity, T::class.java) as T
    }
}