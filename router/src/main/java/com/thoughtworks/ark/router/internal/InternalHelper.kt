package com.thoughtworks.ark.router.internal

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import zlc.season.claritypotion.ClarityPotion

object InternalHelper {
    internal const val SCHEME_REQUEST = "router_request"

    private val internalScope by lazy { MainScope() }

    internal val application: Application
        get() = ClarityPotion.application

    internal val context: Context
        get() = activity ?: ClarityPotion.context

    internal val activity: Activity?
        get() = ClarityPotion.activity

    internal val fragmentActivity: FragmentActivity?
        get() = with(activity) {
            if (this != null && this is FragmentActivity) {
                this
            } else {
                null
            }
        }

    fun Activity.setActivityResult(bundle: Bundle) {
        if (bundle.isEmpty) return
        setResult(RESULT_OK, Intent().apply { putExtras(bundle) })
    }

    fun Activity.contentView(): ViewGroup {
        return findViewById(android.R.id.content)
    }

    fun Context.findActivity(): Activity? {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        return null
    }

    fun Context.findFragmentActivity(): FragmentActivity? {
        val activity = findActivity()
        if (activity is FragmentActivity) {
            return activity
        }
        return null
    }

    fun Context.findScope(): CoroutineScope {
        val fragmentActivity = findFragmentActivity()
        return fragmentActivity?.lifecycleScope ?: internalScope
    }
}