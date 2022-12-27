package com.thoughtworks.ark.router

import android.app.Activity
import android.os.Bundle
import com.thoughtworks.ark.router.internal.InternalHelper.application
import com.thoughtworks.ark.router.internal.key
import zlc.season.claritypotion.ActivityLifecycleCallbacksAdapter

object SchemeLauncherManager {
    private const val OLD_ACTIVITY_KEY = "old_activity_key"

    private val launcherMap = mutableMapOf<String, MutableList<SchemeLauncher>>()
    private val saveInstanceStateMap = mutableMapOf<String, Boolean>()

    init {
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
                super.onActivityPreCreated(activity, savedInstanceState)
                if (savedInstanceState != null) {
                    val oldKey = savedInstanceState.getString(OLD_ACTIVITY_KEY)
                    if (!oldKey.isNullOrEmpty()) {
                        updateKey(oldKey, activity.key())
                    }
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                super.onActivitySaveInstanceState(activity, outState)
                val key = activity.key()
                if (containsKey(key)) {
                    outState.putString(OLD_ACTIVITY_KEY, key)
                }
                updateSaveInstanceState(key)
            }

            override fun onActivityDestroyed(activity: Activity) {
                super.onActivityDestroyed(activity)
                handleActivityDestroy(activity.key())
            }
        })
    }

    @Synchronized
    private fun updateSaveInstanceState(key: String) {
        saveInstanceStateMap[key] = true
    }

    @Synchronized
    private fun handleActivityDestroy(key: String) {
        if (saveInstanceStateMap[key] == null) {
            launcherMap.remove(key)
        }
        saveInstanceStateMap.remove(key)
    }

    @Synchronized
    private fun updateKey(oldKey: String, newKey: String) {
        val oldLauncher = launcherMap.remove(oldKey)
        oldLauncher?.let {
            launcherMap[newKey] = oldLauncher
        }
    }

    @Synchronized
    fun containsKey(key: String): Boolean {
        return launcherMap[key] != null
    }

    @Synchronized
    fun addLauncher(key: String, launcher: SchemeLauncher) {
        val list = launcherMap.getOrPut(key) { mutableListOf() }
        if (list.find { it.schemeRequest.scheme == launcher.schemeRequest.scheme } == null) {
            list.add(launcher)
        }
    }

    @Synchronized
    fun getLauncher(key: String, scheme: String): SchemeLauncher? {
        return launcherMap[key]?.find { it.schemeRequest.scheme == scheme }
    }
}