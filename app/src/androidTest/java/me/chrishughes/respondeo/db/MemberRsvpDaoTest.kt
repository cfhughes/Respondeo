package me.chrishughes.respondeo.db

import android.database.sqlite.SQLiteException
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.chrishughes.respondeo.util.TestUtil
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MemberRsvpDaoTest : DbTest() {

    @Test
    fun insertMemberRsvpWithoutEvent() {
        val event = TestUtil.createEvent("45678", "Event1", "A little event");
        val member = TestUtil.createMember(55)
        db.memberDao().insert(member)
        val memberRsvp = TestUtil.createMemberRsvp(event,member)
        try {
            db.memberRsvpDao().insert(memberRsvp)
            throw AssertionError("must fail because event does not exist")
        } catch (ex: SQLiteException) {
        }

    }

}