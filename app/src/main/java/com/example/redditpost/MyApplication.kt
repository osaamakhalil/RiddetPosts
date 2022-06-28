package com.example.redditpost

import android.app.Application
import com.example.redditpost.di.components.AppComponent
import com.example.redditpost.di.components.DaggerAppComponent


class MyApplication : Application() {

    private val appComponent: AppComponent by lazy {
       DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        provideAppComponent().inject(this)
    }

    fun provideAppComponent() = appComponent
}