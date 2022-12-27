package zlc.season.butterfly

import androidx.core.os.bundleOf
import zlc.season.butterfly.internal.parseScheme
import zlc.season.butterfly.internal.parseSchemeParams

object Butterfly {
    fun enableLog(flag: Boolean) {
        zlc.season.butterfly.internal.enableLog = flag
    }

    fun scheme(scheme: String): SchemeHandler {
        val realScheme = parseScheme(scheme)
        val request = ButterflyCore.queryScheme(realScheme).apply {
            val params = parseSchemeParams(scheme)
            bundle.putAll(bundleOf(*params))
        }
        return SchemeHandler(request)
    }

    fun back(vararg result: Pair<String, Any?>): SchemeRequest? {
        return ButterflyCore.dispatchBack(bundleOf(*result))
    }

    // avoid inline
    val SERVICE_LAMBDA: (String, Class<*>) -> Any = { identity, cls ->
        val real = identity.ifEmpty { cls.simpleName }
        var request = ButterflyCore.queryService(real)
        if (request.className.isEmpty()) {
            request = request.copy(className = cls.name)
        }
        ButterflyCore.dispatchService(request)
    }

    inline fun <reified T> service(
        identity: String = "",
        noinline func: (String, Class<*>) -> Any = SERVICE_LAMBDA
    ): T {
        return func(identity, T::class.java) as T
    }
}