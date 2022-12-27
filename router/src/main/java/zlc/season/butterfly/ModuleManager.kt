package zlc.season.butterfly

import zlc.season.butterfly.module.Module

class ModuleManager {
    private val modules = mutableListOf<Module>()

    fun addModule(vararg module: Module) {
        module.forEach {
            modules.add(it)
        }
    }

    fun removeModule(module: Module) {
        modules.remove(module)
    }

    fun queryScheme(scheme: String): SchemeRequest {
        var result = ""
        modules.forEach {
            val find = it.getScheme()[scheme]
            if (find != null) {
                result = find.name
                return@forEach
            }
        }
        return SchemeRequest(scheme, result)
    }

    fun queryService(identity: String): ServiceRequest {
        var className = ""
        var implClassName = ""
        var isSingleton = true

        modules.forEach {
            if (className.isEmpty()) {
                val serviceMap = it.getService()
                val temp = serviceMap[identity]
                if (temp != null) {
                    className = temp.name
                }
            }
            if (implClassName.isEmpty()) {
                val implMap = it.getServiceImpl()
                val temp = implMap[identity]
                if (temp != null) {
                    implClassName = temp.cls.name
                    isSingleton = temp.singleton
                }
            }

            if (className.isNotEmpty() && implClassName.isNotEmpty()) {
                return@forEach
            }
        }
        return ServiceRequest(identity, className, implClassName, isSingleton)
    }
}