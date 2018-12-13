package me.chrishughes.respondeo.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Event (
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("local_time")
    var time: String,
    @field:SerializedName("description")
    var description: String,
    var groupurl: String,
    var rsvpResponse: String
)