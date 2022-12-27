package zlc.season.butterfly

class DefaultButterflyInterceptor(
    private val interceptor: suspend (SchemeRequest) -> Unit
) : ButterflyInterceptor {
    override suspend fun shouldIntercept(request: SchemeRequest): Boolean {
        return true
    }

    override suspend fun intercept(request: SchemeRequest) {
        interceptor(request)
    }
}