package com.thoughtworks.ark.router

class DefaultRouterInterceptor(
    private val interceptor: suspend (SchemeRequest) -> Unit
) : RouterInterceptor {
    override suspend fun shouldIntercept(request: SchemeRequest): Boolean {
        return true
    }

    override suspend fun intercept(request: SchemeRequest) {
        interceptor(request)
    }
}