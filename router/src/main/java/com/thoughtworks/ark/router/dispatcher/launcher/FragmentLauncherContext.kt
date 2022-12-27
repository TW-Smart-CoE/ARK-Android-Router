package com.thoughtworks.ark.router.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.backstack.BackStackEntryManager
import com.thoughtworks.ark.router.group.GroupEntryManager

class FragmentLauncherContext : FragmentModeLauncher, FragmentGroupLauncher {
    private val standardLauncher = StandardLauncher()
    private val clearTopLauncher = ClearTopLauncher()
    private val singleTopLauncher = SingleTopLauncher()

    private val groupLauncher = GroupLauncher()

    fun FragmentActivity.launch(
        backStackEntryManager: BackStackEntryManager,
        groupEntryManager: GroupEntryManager,
        request: SchemeRequest
    ): Fragment {
        return if (request.groupId.isNotEmpty()) {
            launch(groupEntryManager, request)
        } else {
            launch(backStackEntryManager, request)
        }
    }

    override fun FragmentActivity.launch(backStackEntryManager: BackStackEntryManager, request: SchemeRequest): Fragment {
        return if (request.clearTop) {
            with(clearTopLauncher) { launch(backStackEntryManager, request) }
        } else if (request.singleTop) {
            with(singleTopLauncher) { launch(backStackEntryManager, request) }
        } else {
            with(standardLauncher) { launch(backStackEntryManager, request) }
        }
    }

    override fun FragmentActivity.launch(groupEntryManager: GroupEntryManager, request: SchemeRequest): Fragment {
        return with(groupLauncher) { launch(groupEntryManager, request) }
    }
}