package com.thoughtworks.ark.router

import android.content.Context
import android.os.Bundle

interface Action {
    fun doAction(context: Context, scheme: String, data: Bundle = Bundle())
}