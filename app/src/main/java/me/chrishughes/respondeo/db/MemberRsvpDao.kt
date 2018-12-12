package me.chrishughes.respondeo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.chrishughes.respondeo.vo.MemberRsvp

@Dao
abstract class MemberRsvpDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg rsvps: MemberRsvp)
    @Query("DELETE FROM memberrsvp WHERE eventId = :eventId")
    abstract fun deleteAllForEvent(eventId: String)
    @Query("DELETE FROM memberrsvp")
    abstract fun deleteAll()
}