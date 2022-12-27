@file:OptIn(FlowPreview::class)

package com.thoughtworks.ark.router

import android.os.Bundle
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import com.thoughtworks.ark.router.dispatcher.SchemeDispatcher
import com.thoughtworks.ark.router.dispatcher.ServiceDispatcher
import com.thoughtworks.ark.router.module.Module

object RouterCore {
    private val moduleManager by lazy { ModuleManager() }
    private val interceptorManager by lazy { InterceptorManager() }
    private val schemeDispatcher by lazy { SchemeDispatcher() }
    private val serviceDispatcher by lazy { ServiceDispatcher() }

    fun addModuleName(moduleName: String) {
        try {
            val cls = Class.forName(moduleName)
            if (Module::class.java.isAssignableFrom(cls)) {
                val module = cls.newInstance() as Module
                addModule(module)
            }
        } catch (ignore: Exception) {
            //ignore
        }
    }

    fun addModule(module: Module) = moduleManager.addModule(module)

    fun removeModule(module: Module) = moduleManager.removeModule(module)

    fun addInterceptor(interceptor: RouterInterceptor) = interceptorManager.addInterceptor(interceptor)

    fun removeInterceptor(interceptor: RouterInterceptor) = interceptorManager.removeInterceptor(interceptor)

    fun queryScheme(scheme: String): SchemeRequest = moduleManager.queryScheme(scheme)

    fun queryService(identity: String): ServiceRequest = moduleManager.queryService(identity)

    fun dispatchScheme(request: SchemeRequest, interceptorManager: InterceptorManager): Flow<Result<Bundle>> {
        return flowOf(Unit).onEach {
            if (request.enableGlobalInterceptor) {
                interceptorManager.intercept(request)
            }
        }.onEach {
            interceptorManager.intercept(request)
        }.flatMapConcat {
            schemeDispatcher.dispatch(request)
        }
    }

    fun dispatchBack(bundle: Bundle): SchemeRequest? {
        return schemeDispatcher.back(bundle)
    }

    fun dispatchService(serviceRequest: ServiceRequest): Any {
        return serviceDispatcher.dispatch(serviceRequest)
    }
}