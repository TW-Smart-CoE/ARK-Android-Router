package com.thoughtworks.ark.router

class InterceptorManager {
    private val interceptorList = mutableListOf<RouterInterceptor>()

    fun addInterceptor(interceptor: RouterInterceptor) {
        interceptorList.add(interceptor)
    }

    fun removeInterceptor(interceptor: RouterInterceptor) {
        interceptorList.remove(interceptor)
    }

    suspend fun intercept(schemeRequest: SchemeRequest): SchemeRequest {
        val tempInterceptorList = mutableListOf<RouterInterceptor>()
        tempInterceptorList.addAll(interceptorList)

        var tempRequest = schemeRequest

        tempInterceptorList.forEach {
            if (it.shouldIntercept(tempRequest)) {
                tempRequest = it.intercept(tempRequest)
            }
        }

        return tempRequest
    }
}