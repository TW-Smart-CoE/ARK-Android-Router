package com.thoughtworks.ark.router.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.backstack.BackStackEntryManager
import com.thoughtworks.ark.router.internal.removeFragment

class ClearTopLauncher : NonStandardLauncher() {
    override fun FragmentActivity.launch(backStackEntryManager: BackStackEntryManager, request: SchemeRequest): Fragment {
        val topEntryList = backStackEntryManager.getTopEntryList(this, request)
        return if (topEntryList.isEmpty()) {
            standardLaunch(backStackEntryManager, request)
        } else {
            val targetEntry = topEntryList.removeFirst()
            topEntryList.forEach {
                removeFragment(it.request.uniqueId)
            }
            backStackEntryManager.removeEntries(this, topEntryList)
            tryLaunch(backStackEntryManager, targetEntry.request, request)
        }
    }
}