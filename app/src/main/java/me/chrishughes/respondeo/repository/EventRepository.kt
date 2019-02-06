package me.chrishughes.respondeo.repository

import androidx.lifecycle.LiveData
import me.chrishughes.respondeo.AppExecutors
import me.chrishughes.respondeo.api.ApiResponse
import me.chrishughes.respondeo.api.AuthInfo
import me.chrishughes.respondeo.api.EventService
import me.chrishughes.respondeo.db.EventDao
import me.chrishughes.respondeo.db.EventDb
import me.chrishughes.respondeo.db.MemberDao
import me.chrishughes.respondeo.db.MemberRsvpDao
import me.chrishughes.respondeo.util.RateLimiter
import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.Member
import me.chrishughes.respondeo.vo.MemberRsvp
import me.chrishughes.respondeo.vo.Resource
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val db: EventDb,
    private val eventDao: EventDao,
    private val memberDao: MemberDao,
    private val memberRsvpDao: MemberRsvpDao,
    private val eventService: EventService,
    private val authInfo: AuthInfo
){

    private val eventListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadEvents(): LiveData<Resource<List<Event>>> {
        return object : NetworkBoundResource<List<Event>, List<Event>>(appExecutors) {
            override fun saveCallResult(item: List<Event>) {
                eventDao.deleteAll()
                eventDao.insertEvents(item)
            }

            override fun shouldFetch(data: List<Event>?): Boolean {
                return data == null || eventListRateLimit.shouldFetch("EVENTS")
            }

            override fun loadFromDb() = eventDao.loadEvents()

            //TODO How do I convert this?
            override fun createCall() = eventService.getUpcomingEvents(
                "Bearer " + authInfo.accessToken,
                "self",
                90
            )

            override fun onFetchFailed() {
                eventListRateLimit.reset("EVENTS")
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
                authorization = "Bearer " + authInfo.accessToken
                )
        }.asLiveData()
    }

    private val rsvpListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    fun loadRsvps(groupName: String, id: String): LiveData<Resource<List<Member>>> {
        return object : NetworkBoundResource<List<Member>, List<Member>>(appExecutors) {
            override fun saveCallResult(item: List<Member>) {
                memberRsvpDao.deleteAllForEvent(id)
                memberDao.insert(item)
                item.forEach {
                    memberRsvpDao.insert(MemberRsvp(
                        eventId = id,
                        memberId = it.id,
                        guests = it.guests
                    ))
                }

            }

            override fun shouldFetch(data: List<Member>?): Boolean {
                return data == null || data.isEmpty() || rsvpListRateLimit.shouldFetch(id)
            }

            override fun loadFromDb() = memberDao.rsvpsForEvent(id)

            override fun createCall()= eventService.getRsvps(
                authorization = "Bearer " + authInfo.accessToken,
                urlName = groupName,
                id = id,
                response = "yes"
            )
        }.asLiveData()
    }
}