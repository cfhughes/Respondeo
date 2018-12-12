package me.chrishughes.respondeo.vo

import androidx.room.Entity

@Entity(
    primaryKeys = ["id"]
)
data class Member(
    var id: Long,
    var memberName: String,
    var photoLink: String
)