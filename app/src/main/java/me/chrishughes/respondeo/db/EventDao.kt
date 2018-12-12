package me.chrishughes.respondeo.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.chrishughes.respondeo.vo.Event

@Dao
abstract class EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg repos: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertEvents(repositories: List<Event>)

    @Query(
        """SELECT * FROM Event"""
    )
    abstract fun loadEvents(): LiveData<List<Event>>

    @Query("DELETE FROM Event")
    abstract fun deleteAll()

    @Query("SELECT * FROM Event WHERE id = :id")
    abstract fun load(id: String): LiveData<Event>

}
