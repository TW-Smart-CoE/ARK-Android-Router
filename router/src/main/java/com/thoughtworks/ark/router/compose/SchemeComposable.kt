package com.thoughtworks.ark.router.compose

import android.os.Bundle
import androidx.compose.runtime.Composable

open class SchemeComposable(
    open val composable: (@Composable () -> Unit)? = null,
    open val paramsComposable: (@Composable (Bundle) -> Unit)? = null,
    open val viewModelComposable: (@Composable (Any) -> Unit)? = null,
    open val paramsViewModelComposable: (@Composable (Bundle, Any) -> Unit)? = null,
    open val viewModelClass: String = ""
) {
    constructor() : this(
        null,
        null,
        null,
        null,
        ""
    )
}