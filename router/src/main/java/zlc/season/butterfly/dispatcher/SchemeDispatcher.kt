package zlc.season.butterfly.dispatcher

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.Action
import zlc.season.butterfly.SchemeRequest
import zlc.season.butterfly.backstack.BackStackEntryManager
import zlc.season.butterfly.compose.ComposeDispatcher
import zlc.season.butterfly.compose.SchemeComposable
import zlc.season.butterfly.group.GroupEntryManager
import zlc.season.butterfly.internal.ButterflyHelper
import zlc.season.butterfly.internal.logw

class SchemeDispatcher {
    private val dispatcherMaps = LinkedHashMap<Class<*>, InnerDispatcher>()

    private val backStackEntryManager = BackStackEntryManager()
    private val groupEntryManager = GroupEntryManager()

    init {
        dispatcherMaps.apply {
            putAll(
                listOf(
                    Action::class.java to ActionDispatcher,
                    FragmentActivity::class.java to ActivityDispatcher(backStackEntryManager),
                    DialogFragment::class.java to DialogFragmentDispatcher(backStackEntryManager),
                    Fragment::class.java to FragmentDispatcher(backStackEntryManager, groupEntryManager),
                    SchemeComposable::class.java to ComposeDispatcher(backStackEntryManager, groupEntryManager)
                )
            )
            put(Any::class.java, NoneDispatcher)
        }
    }

    suspend fun dispatch(request: SchemeRequest): Flow<Result<Bundle>> {
        if (request.className.isEmpty()) {
            "Scheme --> dispatch failed! Class not found!".logw()
            return flowOf(Result.failure(IllegalStateException("Scheme class not found!")))
        }

        val fragmentActivity = ButterflyHelper.fragmentActivity
        return if (fragmentActivity == null) {
            findDispatcher(request).dispatch(ButterflyHelper.context, request)
        } else {
            findDispatcher(request).dispatch(fragmentActivity, request)
        }
    }

    fun back(bundle: Bundle): SchemeRequest? {
        val fragmentActivity = ButterflyHelper.fragmentActivity
        if (fragmentActivity == null) {
            "Scheme --> back failed! Activity not found".logw()
            return null
        }

        val topEntry = backStackEntryManager.removeTopEntry(fragmentActivity) ?: return null
        findDispatcher(topEntry.request).back(fragmentActivity, topEntry, bundle)

        dispatcherMaps.values.forEach { it.onBack(fragmentActivity, topEntry) }

        return topEntry.request
    }

    private fun findDispatcher(request: SchemeRequest): InnerDispatcher {
        val cls = Class.forName(request.className)
        return dispatcherMaps[dispatcherMaps.keys.find { it.isAssignableFrom(cls) }]!!
    }
}