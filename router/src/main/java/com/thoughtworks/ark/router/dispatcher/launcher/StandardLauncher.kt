package com.thoughtworks.ark.router.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.backstack.BackStackEntry
import com.thoughtworks.ark.router.backstack.BackStackEntryManager
import com.thoughtworks.ark.router.internal.showNewFragment

class StandardLauncher : FragmentModeLauncher {
    override fun FragmentActivity.launch(backStackEntryManager: BackStackEntryManager, request: SchemeRequest): Fragment {
        if (request.enableBackStack) {
            backStackEntryManager.addEntry(this, BackStackEntry(request))
        }
        return showNewFragment(request)
    }
}