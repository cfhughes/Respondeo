package me.chrishughes.respondeo.api

import androidx.lifecycle.LiveData
import com.google.gson.annotations.SerializedName
import me.chrishughes.respondeo.vo.Event
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface EventService {

    @GET("2/concierge")
    fun getUpcomingEvents(
        @Header("Authorization") authorization: String,
        @Query("fields") fields: String
    ): LiveData<ApiResponse<List<Event>>>

    @GET("/{urlname}/events/{id}")
    fun getEvent(
        @Header("Authorization") authorization: String,
        @Path("urlname") urlName: String,
        @Path("id") id: String
    ): LiveData<ApiResponse<Event>>

    data class Results<T> (
        @field:SerializedName("results")
        val results: List<T>? = null
    )

}