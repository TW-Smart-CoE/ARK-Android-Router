package zlc.season.butterfly

interface ButterflyInterceptor {
    suspend fun shouldIntercept(request: SchemeRequest): Boolean
    suspend fun intercept(request: SchemeRequest)
}