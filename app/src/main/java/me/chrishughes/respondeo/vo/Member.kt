package me.chrishughes.respondeo.vo

import androidx.room.Entity
import androidx.room.Ignore

@Entity(
    primaryKeys = ["id"]
)
data class Member(
    var id: Long,
    var memberName: String,
    var photoLink: String,
    var guests: Int
)