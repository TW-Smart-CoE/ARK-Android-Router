package com.thoughtworks.ark.router

interface RouterInterceptor {
    suspend fun shouldIntercept(request: SchemeRequest): Boolean
    suspend fun intercept(request: SchemeRequest)
}