package com.thoughtworks.ark.router.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thoughtworks.ark.router.Router

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Router.scheme("main/fragment").route(this)
    }
}