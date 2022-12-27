package com.thoughtworks.ark.router.module

import com.thoughtworks.ark.router.annotation.ServiceData

interface Module {
    fun getScheme(): Map<String, Class<*>>
    fun getService(): Map<String, Class<*>>
    fun getServiceImpl(): Map<String, ServiceData>
}