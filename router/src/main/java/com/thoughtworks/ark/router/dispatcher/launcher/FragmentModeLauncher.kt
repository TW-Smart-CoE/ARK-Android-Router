package com.thoughtworks.ark.router.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.backstack.BackStackEntryManager

interface FragmentModeLauncher {
    fun FragmentActivity.launch(backStackEntryManager: BackStackEntryManager, request: SchemeRequest): Fragment
}