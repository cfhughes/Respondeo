/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.chrishughes.respondeo.di

import android.app.Application
import androidx.room.Room
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import me.chrishughes.respondeo.api.AuthInfo
import me.chrishughes.respondeo.api.EventService
import me.chrishughes.respondeo.db.EventDao
import me.chrishughes.respondeo.db.EventDb
import me.chrishughes.respondeo.db.MemberDao
import me.chrishughes.respondeo.db.MemberRsvpDao
import me.chrishughes.respondeo.util.LiveDataCallAdapterFactory
import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.EventDeserializer
import me.chrishughes.respondeo.vo.Member
import me.chrishughes.respondeo.vo.RsvpDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun providesAuthInfo(): AuthInfo {
        return AuthInfo("")
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        //val logging = HttpLoggingInterceptor()
        //logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
        //httpClient.addInterceptor(logging)
        return httpClient.build()
    }

    @Singleton
    @Provides
    fun provideEventService(httpClient: OkHttpClient): EventService {

        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Event::class.java, EventDeserializer())
        gsonBuilder.registerTypeAdapter(Member::class.java, RsvpDeserializer())

        return Retrofit.Builder()
            .baseUrl("https://api.meetup.com/")
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(httpClient)
            .build()
            .create(EventService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): EventDb {
        return Room
            .databaseBuilder(app, EventDb::class.java, "event.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideEventDao(db: EventDb): EventDao {
        return db.eventDao()
    }

    @Singleton
    @Provides
    fun provideMemberDao(db: EventDb): MemberDao {
        return db.memberDao()
    }

    @Singleton
    @Provides
    fun provideMemberRsvpDao(db: EventDb): MemberRsvpDao {
        return db.memberRsvpDao()
    }

    /*@Singleton
    @Provides
    fun provideRepoDao(db: EventDb): RepoDao {
        return db.repoDao()
    }*/
}
