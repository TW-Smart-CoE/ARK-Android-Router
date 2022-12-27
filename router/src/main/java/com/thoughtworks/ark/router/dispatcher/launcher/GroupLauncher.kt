package com.thoughtworks.ark.router.dispatcher.launcher

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.group.GroupEntry
import com.thoughtworks.ark.router.group.GroupEntryManager
import com.thoughtworks.ark.router.internal.findFragment
import com.thoughtworks.ark.router.internal.hideFragment
import com.thoughtworks.ark.router.internal.showFragment
import com.thoughtworks.ark.router.internal.showNewFragment

class GroupLauncher : FragmentGroupLauncher {
    override fun FragmentActivity.launch(groupEntryManager: GroupEntryManager, request: SchemeRequest): Fragment {
        val list = groupEntryManager.getGroupList(this, request.groupId)
        list.forEach { entity ->
            findFragment(entity.request)?.also { hideFragment(it) }
        }

        val targetEntry = list.find { it.request.className == request.className }
        val targetFragment = targetEntry?.run {
            findFragment(targetEntry.request)
        }

        return if (targetFragment == null) {
            if (targetEntry == null) {
                groupEntryManager.addEntry(this, GroupEntry(request))
            }
            showNewFragment(request)
        } else {
            if (targetFragment is OnFragmentNewArgument) {
                targetFragment.onNewArgument(request.bundle)
            }
            showFragment(targetFragment)
            targetFragment
        }
    }
}