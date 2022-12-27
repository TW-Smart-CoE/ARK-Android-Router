package com.thoughtworks.ark.router.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.backstack.BackStackEntryManager
import com.thoughtworks.ark.router.internal.findFragment
import com.thoughtworks.ark.router.internal.showFragment

abstract class NonStandardLauncher : FragmentModeLauncher {
    private val standardLauncher = StandardLauncher()

    protected fun FragmentActivity.standardLaunch(backStackEntryManager: BackStackEntryManager, request: SchemeRequest): Fragment {
        return with(standardLauncher) {
            launch(backStackEntryManager, request)
        }
    }

    protected fun FragmentActivity.tryLaunch(
        backStackEntryManager: BackStackEntryManager,
        oldRequest: SchemeRequest,
        newRequest: SchemeRequest
    ): Fragment {
        val target = findFragment(oldRequest)
        return if (target == null) {
            standardLaunch(backStackEntryManager, newRequest)
        } else {
            if (target is OnFragmentNewArgument) {
                target.onNewArgument(newRequest.bundle)
            }
            showFragment(target)
            target
        }
    }
}