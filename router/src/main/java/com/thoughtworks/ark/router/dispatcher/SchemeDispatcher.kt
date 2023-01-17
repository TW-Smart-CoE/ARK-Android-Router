package com.thoughtworks.ark.router.dispatcher

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.thoughtworks.ark.router.Action
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.backstack.BackStackEntryManager
import com.thoughtworks.ark.router.compose.ComposeDispatcher
import com.thoughtworks.ark.router.compose.SchemeComposable
import com.thoughtworks.ark.router.group.GroupEntryManager
import com.thoughtworks.ark.router.internal.InternalHelper
import com.thoughtworks.ark.router.internal.InternalHelper.findFragmentActivity
import com.thoughtworks.ark.router.internal.logw
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SchemeDispatcher {
    private val dispatcherMaps = LinkedHashMap<Class<*>, InnerDispatcher>()

    private val backStackEntryManager = BackStackEntryManager()
    private val groupEntryManager = GroupEntryManager()

    init {
        dispatcherMaps.apply {
            putAll(
                listOf(
                    Action::class.java to ActionDispatcher,
                    FragmentActivity::class.java to ActivityDispatcher(backStackEntryManager),
                    DialogFragment::class.java to DialogFragmentDispatcher(backStackEntryManager),
                    Fragment::class.java to FragmentDispatcher(backStackEntryManager, groupEntryManager),
                    SchemeComposable::class.java to ComposeDispatcher(backStackEntryManager, groupEntryManager)
                )
            )
            put(Any::class.java, NoneDispatcher)
        }
    }

    suspend fun dispatch(context: Context, request: SchemeRequest): Flow<Result<Bundle>> {
        if (request.className.isEmpty()) {
            "Scheme --> dispatch failed! Class not found!".logw()
            return flowOf(Result.failure(IllegalStateException("Scheme class not found!")))
        }

        val fragmentActivity = context.findFragmentActivity()
        return if (fragmentActivity == null) {
            findDispatcher(request).dispatch(context, request)
        } else {
            findDispatcher(request).dispatch(fragmentActivity, request)
        }
    }

    fun back(bundle: Bundle): SchemeRequest? {
        val fragmentActivity = InternalHelper.fragmentActivity
        if (fragmentActivity == null) {
            "Scheme --> back failed! Activity not found".logw()
            return null
        }

        val topEntry = backStackEntryManager.removeTopEntry(fragmentActivity) ?: return null
        findDispatcher(topEntry.request).back(fragmentActivity, topEntry, bundle)

        dispatcherMaps.values.forEach { it.onBack(fragmentActivity, topEntry) }

        return topEntry.request
    }

    private fun findDispatcher(request: SchemeRequest): InnerDispatcher {
        val cls = Class.forName(request.className)
        return dispatcherMaps[dispatcherMaps.keys.find { it.isAssignableFrom(cls) }]!!
    }
}