package me.chrishughes.respondeo.api

import androidx.lifecycle.LiveData
import com.google.gson.annotations.SerializedName
import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.Member
import me.chrishughes.respondeo.vo.RsvpResult
import retrofit2.Call
import retrofit2.http.*

interface EventService {

    @GET("/self/calendar")
    fun getUpcomingEvents(
        @Header("Authorization") authorization: String,
        @Query("fields") fields: String,
        @Query("page") pageSize: Int
    ): LiveData<ApiResponse<List<Event>>>

    @GET("/{urlname}/events/{id}")
    fun getEvent(
        @Header("Authorization") authorization: String,
        @Path("urlname") urlName: String,
        @Path("id") id: String
    ): LiveData<ApiResponse<Event>>

    @GET("/{urlname}/events/{id}/rsvps")
    fun getRsvps(
        @Header("Authorization") authorization: String,
        @Path("urlname") urlName: String,
        @Path("id") id: String,
        @Query("response") response: String
    ): LiveData<ApiResponse<List<Member>>>

    @POST("/{urlname}/events/{id}/rsvps")
    fun sendRsvp(
        @Header("Authorization") authorization: String,
        @Path("urlname") urlName: String,
        @Path("id") id: String,
        @Query("response") response: String
    ): Call<RsvpResult>

    data class Results<T> (
        @field:SerializedName("results")
        val results: List<T>? = null
    )

}