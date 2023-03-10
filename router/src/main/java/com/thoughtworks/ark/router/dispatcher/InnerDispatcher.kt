package com.thoughtworks.ark.router.dispatcher

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import com.thoughtworks.ark.router.SchemeRequest
import com.thoughtworks.ark.router.backstack.BackStackEntry

interface InnerDispatcher {
    suspend fun dispatch(context: Context, request: SchemeRequest): Flow<Result<Bundle>> = emptyFlow()

    suspend fun dispatch(activity: FragmentActivity, request: SchemeRequest): Flow<Result<Bundle>> = emptyFlow()

    fun back(activity: FragmentActivity, topEntry: BackStackEntry, bundle: Bundle) {}

    fun onBack(activity: FragmentActivity, topEntry: BackStackEntry) {}
}