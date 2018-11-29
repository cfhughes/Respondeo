package me.chrishughes.respondeo.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import me.chrishughes.respondeo.AppExecutors
import me.chrishughes.respondeo.api.ApiSuccessResponse
import me.chrishughes.respondeo.api.AuthInfo
import me.chrishughes.respondeo.api.EventService
import me.chrishughes.respondeo.db.EventDao
import me.chrishughes.respondeo.db.EventDb
import me.chrishughes.respondeo.util.RateLimiter
import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.Resource
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: EventDb,
    private val eventDao: EventDao,
    private val eventService: EventService,
    private val authInfo: AuthInfo
){

    private val eventListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadEvents(owner: String): LiveData<Resource<List<Event>>> {
        return object : NetworkBoundResource<List<Event>, List<Event>>(appExecutors) {
            override fun saveCallResult(item: List<Event>) {
                eventDao.insertEvents(item)
            }

            override fun shouldFetch(data: List<Event>?): Boolean {
                return data == null || eventListRateLimit.shouldFetch(owner)
            }

            override fun loadFromDb() = eventDao.loadEvents()

            //TODO How do I convert this?
            override fun createCall() = eventService.getUpcomingEvents("","")

            override fun onFetchFailed() {
                eventListRateLimit.reset(owner)
            }
        }.asLiveData()
    }

    fun loadEvent(id: String, urlName: String): LiveData<Resource<Event>> {
        return object : NetworkBoundResource<Event, Event>(appExecutors) {
            override fun saveCallResult(item: Event) {
                //Log.v("EventRepo", "id: "+ item.id)
                eventDao.insert(item)
            }

            override fun shouldFetch(data: Event?) = data == null

            override fun loadFromDb() = eventDao.load(
                id = id
            )

            override fun createCall() = eventService.getEvent(
                    urlName = urlName,
                    id = id,
                    //TODO Authorization
                    authorization = authInfo.accessToken
                )
        }.asLiveData()
    }
}