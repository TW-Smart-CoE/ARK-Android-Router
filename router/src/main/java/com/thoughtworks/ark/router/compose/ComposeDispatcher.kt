package com.thoughtworks.ark.router.compose

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.internal.InternalHelper.setActivityResult
import com.thoughtworks.ark.router.backstack.BackStackEntry
import com.thoughtworks.ark.router.backstack.BackStackEntryManager
import com.thoughtworks.ark.router.compose.Utils.isActivityEntry
import com.thoughtworks.ark.router.compose.Utils.isComposeEntry
import com.thoughtworks.ark.router.compose.launcher.ComposeLauncher
import com.thoughtworks.ark.router.dispatcher.InnerDispatcher
import com.thoughtworks.ark.router.group.GroupEntryManager

class ComposeDispatcher(
    private val backStackEntryManager: BackStackEntryManager,
    private val groupEntryManager: GroupEntryManager
) : InnerDispatcher {
    private val composeLauncher = ComposeLauncher(backStackEntryManager, groupEntryManager)

    override fun back(activity: FragmentActivity, topEntry: BackStackEntry, bundle: Bundle) {
        with(activity) {
            val newTopEntry = backStackEntryManager.getTopEntry(this)
            if (newTopEntry == null || isActivityEntry(newTopEntry)) {
                if (topEntry.request.isRoot) {
                    setActivityResult(bundle)
                    finish()
                } else {
                    with(composeLauncher) {
                        clear()
                    }
                }
            }
        }
    }

    override fun onBack(activity: FragmentActivity, topEntry: BackStackEntry) {
        val newTopEntry = backStackEntryManager.getTopEntry(activity)
        if (newTopEntry != null && isComposeEntry(newTopEntry)) {
            with(composeLauncher) {
                activity.back(newTopEntry.request)
            }
        }
    }

    override suspend fun dispatch(activity: FragmentActivity, request: SchemeRequest): Flow<Result<Bundle>> {
        with(composeLauncher) {
            activity.launch(request)
        }
        return emptyFlow()
    }
}