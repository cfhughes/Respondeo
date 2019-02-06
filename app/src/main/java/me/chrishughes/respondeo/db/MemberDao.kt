package me.chrishughes.respondeo.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.chrishughes.respondeo.vo.Member

@Dao
abstract class MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg members: Member)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(members: List<Member>)
    @Query("SELECT m.id,m.memberName,m.photoLink,r.guests FROM member AS m INNER JOIN MemberRsvp AS r ON m.id = r.memberId AND r.eventId = :eventId")
    abstract fun rsvpsForEvent(eventId: String) : LiveData<List<Member>>
}