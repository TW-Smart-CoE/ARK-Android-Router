package com.thoughtworks.ark.router.compose.launcher

import androidx.fragment.app.FragmentActivity
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.group.GroupEntry
import com.thoughtworks.ark.router.group.GroupEntryManager

class GroupLauncher(private val groupEntryManager: GroupEntryManager) {
    private val commonLauncher = CommonLauncher()

    fun FragmentActivity.launch(request: SchemeRequest) {
        val groupEntryList = groupEntryManager.getGroupList(this, request.groupId)
        val find = groupEntryList.find { it.request.className == request.className }
        if (find == null) {
            groupEntryManager.addEntry(this, GroupEntry(request))
        }

        with(commonLauncher) {
            launch(request)
        }
    }
}