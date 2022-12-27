package zlc.season.butterfly.module

import zlc.season.butterfly.annotation.ServiceData

interface Module {
    fun getScheme(): Map<String, Class<*>>
    fun getService(): Map<String, Class<*>>
    fun getServiceImpl(): Map<String, ServiceData>
}