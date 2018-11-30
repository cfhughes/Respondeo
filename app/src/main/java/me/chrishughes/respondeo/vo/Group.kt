package me.chrishughes.respondeo.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Group(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("urlname")
    val urlname: String
)
