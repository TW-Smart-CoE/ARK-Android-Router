package com.thoughtworks.ark.router.dispatcher

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.backstack.BackStackEntry
import com.thoughtworks.ark.router.backstack.BackStackEntryManager
import com.thoughtworks.ark.router.dispatcher.launcher.FragmentLauncherContext
import com.thoughtworks.ark.router.group.GroupEntryManager
import com.thoughtworks.ark.router.internal.InternalFragment.Companion.awaitFragmentResult
import com.thoughtworks.ark.router.internal.InternalHelper.setActivityResult
import com.thoughtworks.ark.router.internal.findFragment
import com.thoughtworks.ark.router.internal.removeFragment
import com.thoughtworks.ark.router.internal.setFragmentResult

class FragmentDispatcher(
    private val backStackEntryManager: BackStackEntryManager,
    private val groupEntryManager: GroupEntryManager
) : InnerDispatcher {

    private val fragmentLauncherContext = FragmentLauncherContext()

    override fun back(activity: FragmentActivity, topEntry: BackStackEntry, bundle: Bundle) {
        activity.apply {
            val newTopEntry = backStackEntryManager.getTopEntry(this)
            if ((newTopEntry == null || isActivityEntry(newTopEntry)) && topEntry.request.isRoot) {
                setActivityResult(bundle)
                finish()
            } else {
                findFragment(topEntry.request)?.let {
                    if (topEntry.request.needResult) {
                        setFragmentResult(topEntry.request.uniqueTag, bundle)
                    }
                    removeFragment(it)
                }
            }
        }
    }

    override suspend fun dispatch(activity: FragmentActivity, request: SchemeRequest): Flow<Result<Bundle>> {
        with(fragmentLauncherContext) {
            activity.launch(backStackEntryManager, groupEntryManager, request)
        }

        return if (request.needResult) {
            activity.awaitFragmentResult(request.scheme, request.uniqueTag)
        } else {
            emptyFlow()
        }
    }

    private fun isActivityEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.request.className)
        return Activity::class.java.isAssignableFrom(cls)
    }
}