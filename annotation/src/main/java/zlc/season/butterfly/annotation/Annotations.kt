package zlc.season.butterfly.annotation

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Scheme(
    val scheme: String
)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Service(
    val identity: String = ""
)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class ServiceImpl(
    val singleton: Boolean = true,
    val identity: String = ""
)