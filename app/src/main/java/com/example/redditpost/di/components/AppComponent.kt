package com.example.redditpost.di.components

import android.content.Context
import com.example.redditpost.MainActivity
import com.example.redditpost.MyApplication
import com.example.redditpost.di.modules.*
import com.example.redditpost.presentation.view.FavoriteFragment
import com.example.redditpost.presentation.view.HomeFragment
import com.example.redditpost.presentation.view.SearchFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

/**
 * in the Capiter app the class act as dagger component his name
 * is core component and he is in the core module.
 * Most likely you will not need to deal with, but to clarify.
 */

@Singleton
@Component(
    modules = [
        AppModule::class,
        RemoteModule::class,
        LocalModule::class,
        ViewModelsModule::class,
        DataModule::class,
        SchedulersModule::class
    ]
)
interface AppComponent {

    fun inject(app: MyApplication)

    fun inject(activity: MainActivity)

    fun inject(fragment: HomeFragment)

    fun inject(fragment: SearchFragment)

    fun inject(fragment: FavoriteFragment)


    @Component.Factory
    interface Factory {

        fun create(@BindsInstance applicationContext: Context): AppComponent

    }
}