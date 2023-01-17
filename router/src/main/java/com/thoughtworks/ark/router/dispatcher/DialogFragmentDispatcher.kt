package com.thoughtworks.ark.router.dispatcher

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.backstack.BackStackEntry
import com.thoughtworks.ark.router.backstack.BackStackEntryManager
import com.thoughtworks.ark.router.internal.InternalFragment.Companion.awaitFragmentResult
import com.thoughtworks.ark.router.internal.findDialogFragment
import com.thoughtworks.ark.router.internal.setFragmentResult
import com.thoughtworks.ark.router.internal.showDialogFragment

class DialogFragmentDispatcher(val backStackEntryManager: BackStackEntryManager) : InnerDispatcher {

    override fun back(activity: FragmentActivity, topEntry: BackStackEntry, bundle: Bundle) {
        with(activity) {
            val find = findDialogFragment(topEntry.request) ?: return
            if (topEntry.request.needResult) {
                setFragmentResult(topEntry.request.uniqueTag, bundle)
            }
            find.dismissAllowingStateLoss()
        }
    }

    override suspend fun dispatch(activity: FragmentActivity, request: SchemeRequest): Flow<Result<Bundle>> {
        if (request.enableBackStack) {
            backStackEntryManager.addEntry(activity, BackStackEntry(request))
        }

        activity.showDialogFragment(request)

        return if (request.needResult) {
            activity.awaitFragmentResult(request.scheme, request.uniqueTag)
        } else {
            emptyFlow()
        }
    }
}


