package zlc.season.butterfly.compose.launcher

import androidx.fragment.app.FragmentActivity
import zlc.season.butterfly.SchemeRequest
import zlc.season.butterfly.group.GroupEntry
import zlc.season.butterfly.group.GroupEntryManager

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