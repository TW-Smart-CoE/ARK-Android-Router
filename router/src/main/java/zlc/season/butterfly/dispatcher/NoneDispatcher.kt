package zlc.season.butterfly.dispatcher

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import zlc.season.butterfly.SchemeRequest
import zlc.season.butterfly.internal.logw

object NoneDispatcher : InnerDispatcher {
    override suspend fun dispatch(context: Context, request: SchemeRequest): Flow<Result<Bundle>> {
        return error()
    }

    override suspend fun dispatch(activity: FragmentActivity, request: SchemeRequest): Flow<Result<Bundle>> {
        return error()
    }

    private fun error(): Flow<Result<Bundle>> {
        "Scheme --> type error".logw()
        return flowOf(Result.failure(IllegalStateException("Scheme type error")))
    }
}