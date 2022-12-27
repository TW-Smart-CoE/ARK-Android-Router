package zlc.season.butterfly

class InterceptorManager {
    private val interceptorList = mutableListOf<ButterflyInterceptor>()

    fun addInterceptor(interceptor: ButterflyInterceptor) {
        interceptorList.add(interceptor)
    }

    fun removeInterceptor(interceptor: ButterflyInterceptor) {
        interceptorList.remove(interceptor)
    }

    suspend fun intercept(schemeRequest: SchemeRequest) {
        val temp = mutableListOf<ButterflyInterceptor>()
        temp.addAll(interceptorList)

        temp.forEach {
            if (it.shouldIntercept(schemeRequest)) {
                it.intercept(schemeRequest)
            }
        }
    }
}