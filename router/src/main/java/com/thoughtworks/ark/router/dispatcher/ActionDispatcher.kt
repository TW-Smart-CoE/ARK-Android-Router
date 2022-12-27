package com.thoughtworks.ark.router.dispatcher

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import com.thoughtworks.ark.router.Action
import com.thoughtworks.ark.router.SchemeRequest

object ActionDispatcher : InnerDispatcher {
    override suspend fun dispatch(context: Context, request: SchemeRequest): Flow<Result<Bundle>> {
        return handleAction(context, request)
    }

    override suspend fun dispatch(activity: FragmentActivity, request: SchemeRequest): Flow<Result<Bundle>> {
        return handleAction(activity, request)
    }

    private fun handleAction(context: Context, request: SchemeRequest): Flow<Result<Bundle>> {
        val action = createAction(request)
        action.doAction(context, request.scheme, request.bundle)
        return emptyFlow()
    }

    private fun createAction(request: SchemeRequest): Action {
        val cls = Class.forName(request.className)
        return cls.newInstance() as Action
    }
}