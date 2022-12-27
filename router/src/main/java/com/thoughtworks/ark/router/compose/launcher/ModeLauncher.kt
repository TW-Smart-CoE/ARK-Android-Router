package com.thoughtworks.ark.router.compose.launcher

import androidx.fragment.app.FragmentActivity
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.backstack.BackStackEntry
import com.thoughtworks.ark.router.backstack.BackStackEntryManager

class ModeLauncher(private val backStackEntryManager: BackStackEntryManager) {
    private val commonLauncher = CommonLauncher()

    fun FragmentActivity.launch(request: SchemeRequest) {
        if (request.clearTop) {
            clearTopLaunch(request)
        } else if (request.singleTop) {
            singleTopLaunch(request)
        } else {
            standardLaunch(request)
        }
    }

    fun FragmentActivity.launchDirectly(request: SchemeRequest) {
        commonLaunch(request)
    }

    private fun FragmentActivity.standardLaunch(request: SchemeRequest) {
        if (request.enableBackStack) {
            backStackEntryManager.addEntry(this, BackStackEntry(request))
        }

        commonLaunch(request)
    }

    private fun FragmentActivity.clearTopLaunch(request: SchemeRequest) {
        val topEntryList = backStackEntryManager.getTopEntryList(this, request)
        return if (topEntryList.isEmpty()) {
            standardLaunch(request)
        } else {
            topEntryList.removeFirst()
            backStackEntryManager.removeEntries(this, topEntryList)
            commonLaunch(request)
        }
    }

    private fun FragmentActivity.singleTopLaunch(request: SchemeRequest) {
        val topEntry = backStackEntryManager.getTopEntry(this)
        return if (topEntry != null && topEntry.request.className == request.className) {
            commonLaunch(request)
        } else {
            standardLaunch(request)
        }
    }

    private fun FragmentActivity.commonLaunch(request: SchemeRequest) {
        with(commonLauncher) {
            launch(request)
        }
    }
}