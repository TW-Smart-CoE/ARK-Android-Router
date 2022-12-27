package com.thoughtworks.ark.router.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.group.GroupEntryManager

interface FragmentGroupLauncher {
    fun FragmentActivity.launch(groupEntryManager: GroupEntryManager, request: SchemeRequest): Fragment
}