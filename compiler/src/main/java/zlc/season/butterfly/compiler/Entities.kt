package zlc.season.butterfly.compiler

internal data class ServiceImplInfo(val className: String, val singleton: Boolean)

internal data class ComposableInfo(
    val packageName: String,
    val methodName: String,
    val hasBundle: Boolean,
    val viewModelName: String
)