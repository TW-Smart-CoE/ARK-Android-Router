package com.thoughtworks.ark.router

class DefaultRouterInterceptor(
    private val interceptor: suspend (SchemeRequest) -> SchemeRequest
) : RouterInterceptor {
    override suspend fun shouldIntercept(request: SchemeRequest): Boolean {
        return true
    }

    override suspend fun intercept(request: SchemeRequest): SchemeRequest {
        return interceptor(request)
    }
}