package com.thoughtworks.ark.router.compose.launcher

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.compose.SchemeComposable
import com.thoughtworks.ark.router.compose.Utils.composeViewId
import com.thoughtworks.ark.router.internal.InternalHelper.contentView

class CommonLauncher {
    fun FragmentActivity.launch(request: SchemeRequest) {
        var composeView = findViewById<ComposeView>(composeViewId)
        if (composeView == null) {
            composeView = ComposeView(this).apply { id = composeViewId }
            val containerView = findContainerView(request)
            containerView.addView(composeView, LayoutParams(MATCH_PARENT, MATCH_PARENT))
        }
        invokeCompose(composeView, request)
    }

    private fun FragmentActivity.invokeCompose(composeView: ComposeView, request: SchemeRequest) {
        composeView.tag = request.uniqueTag

        val cls = Class.forName(request.className)
        val composable = cls.newInstance() as SchemeComposable
        composeView.setContent {
            if (composable.paramsViewModelComposable != null) {
                composable.paramsViewModelComposable?.invoke(request.bundle, getViewModel(composable))
            } else if (composable.viewModelComposable != null) {
                composable.viewModelComposable?.invoke(getViewModel(composable))
            } else if (composable.paramsComposable != null) {
                composable.paramsComposable?.invoke(request.bundle)
            } else {
                composable.composable?.invoke()
            }
        }
    }

    @Suppress("unchecked_cast")
    private fun FragmentActivity.getViewModel(composable: SchemeComposable): ViewModel {
        return ViewModelProvider(
            viewModelStore,
            defaultViewModelProviderFactory
        )[Class.forName(composable.viewModelClass) as Class<ViewModel>]
    }

    private fun FragmentActivity.findContainerView(request: SchemeRequest): ViewGroup {
        var result: ViewGroup? = null
        if (request.containerViewId != 0) {
            result = findViewById(request.containerViewId)
        }
        if (result == null) {
            result = contentView()
        }
        return result
    }
}