package com.thoughtworks.ark.router.compose

import android.app.Activity
import android.view.View
import com.thoughtworks.ark.router.backstack.BackStackEntry
import com.thoughtworks.ark.router.group.GroupEntry

object Utils {
    val composeViewId = View.generateViewId()

    fun isComposeEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.request.className)
        return SchemeComposable::class.java.isAssignableFrom(cls)
    }

    fun isComposeEntry(entry: GroupEntry): Boolean {
        val cls = Class.forName(entry.request.className)
        return SchemeComposable::class.java.isAssignableFrom(cls)
    }

    fun isActivityEntry(entry: BackStackEntry): Boolean {
        val cls = Class.forName(entry.request.className)
        return Activity::class.java.isAssignableFrom(cls)
    }
}