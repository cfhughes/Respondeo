package me.chrishughes.respondeo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.Member
import me.chrishughes.respondeo.vo.MemberRsvp

@Database(
    entities = [
        Event::class,
        Member::class,
        MemberRsvp::class],
    version = 1,
    exportSchema = false
)
abstract class EventDb : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun memberDao(): MemberDao
    abstract fun memberRsvpDao(): MemberRsvpDao
}