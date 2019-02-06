package me.chrishughes.respondeo;

import android.app.Activity
import android.app.Application
import com.facebook.stetho.Stetho
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import me.chrishughes.respondeo.di.AppInjector
import javax.inject.Inject

class EventApp : Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)
        Stetho.initializeWithDefaults(this);
    }

    override fun activityInjector() = dispatchingAndroidInjector
}
