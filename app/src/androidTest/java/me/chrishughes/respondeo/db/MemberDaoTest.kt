package me.chrishughes.respondeo.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.chrishughes.respondeo.util.LiveDataTestUtil.getValue
import me.chrishughes.respondeo.util.TestUtil
import org.hamcrest.CoreMatchers
import org.junit.runner.RunWith
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MemberDaoTest : DbTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertMembers() {
        val event = TestUtil.createEvent("45678", "Event1", "A little event");
        val m1 = TestUtil.createMember(56)
        val m2 = TestUtil.createMember(57)
        val r1 = TestUtil.createMemberRsvp(event,m1)
        val r2 = TestUtil.createMemberRsvp(event,m2)
        db.beginTransaction()
        try {
            db.eventDao().insert(event)
            db.memberDao().insert(arrayListOf(m1,m2))
            db.memberRsvpDao().insert(r1,r2)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        val list = getValue(db.memberDao().rsvpsForEvent(event.id))
        assertThat(list.size, CoreMatchers.`is`(2))
        val first = list[0]

        assertThat(first.id, CoreMatchers.`is`(56L))
        assertThat(first.memberName, CoreMatchers.`is`(m1.memberName))

        val second = list[1]
        assertThat(second.id, CoreMatchers.`is`(57L))
        assertThat(second.photoLink, CoreMatchers.`is`(m2.photoLink))
    }

}