package me.chrishughes.respondeo.vo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index

@Entity(
    primaryKeys = ["eventId","memberId"],
    foreignKeys = [ForeignKey(
        entity = Member::class,
        parentColumns = ["id"],
        childColumns = ["memberId"]
    ),
    ForeignKey(
        entity = Event::class,
        parentColumns = ["id"],
        childColumns = ["eventId"],
        onDelete = CASCADE
    )],
    indices = [Index("memberId")]
)
data class MemberRsvp(
    val eventId: String,
    val memberId: Long,
    val guests: Int
)