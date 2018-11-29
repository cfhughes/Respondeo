package me.chrishughes.respondeo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.chrishughes.respondeo.vo.Event

@Database(
    entities = [
        Event::class],
    version = 1,
    exportSchema = false
)
abstract class EventDb : RoomDatabase() {

    abstract fun eventDao(): EventDao
}