package com.thoughtworks.ark.router

class InterceptorManager {
    private val interceptorList = mutableListOf<RouterInterceptor>()

    fun addInterceptor(interceptor: RouterInterceptor) {
        interceptorList.add(interceptor)
    }

    fun removeInterceptor(interceptor: RouterInterceptor) {
        interceptorList.remove(interceptor)
    }

    suspend fun intercept(schemeRequest: SchemeRequest) {
        val temp = mutableListOf<RouterInterceptor>()
        temp.addAll(interceptorList)

        temp.forEach {
            if (it.shouldIntercept(schemeRequest)) {
                it.intercept(schemeRequest)
            }
        }
    }
}