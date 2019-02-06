package me.chrishughes.respondeo.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import me.chrishughes.respondeo.util.LiveDataTestUtil.getValue
import me.chrishughes.respondeo.util.TestUtil
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventDaoTest : DbTest() {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun insertAndRead() {
        val id = "45678"
        val event = TestUtil.createEvent(id, "Event", "A big event");
        db.eventDao().insert(event)
        val loaded = getValue(db.eventDao().load(id))
        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.name, CoreMatchers.`is`("Event"))
        MatcherAssert.assertThat(loaded.description, CoreMatchers.`is`("A big event"))
        MatcherAssert.assertThat(loaded.rsvpResponse, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.time, CoreMatchers.notNullValue())
    }




    /*
    @Test
    fun createIfNotExists_exists() {
        val repo = TestUtil.createRepo("foo", "bar", "desc")
        db.repoDao().insert(repo)
        MatcherAssert.assertThat(db.repoDao().createRepoIfNotExists(repo), CoreMatchers.`is`(-1L))
    }

    @Test
    fun createIfNotExists_doesNotExist() {
        val repo = TestUtil.createRepo("foo", "bar", "desc")
        MatcherAssert.assertThat(db.repoDao().createRepoIfNotExists(repo), CoreMatchers.`is`(1L))
    }

    @Test
    fun insertContributorsThenUpdateRepo() {
        val repo = TestUtil.createRepo("foo", "bar", "desc")
        db.repoDao().insert(repo)
        val contributor = TestUtil.createContributor(repo, "aa", 3)
        db.repoDao().insertContributors(listOf(contributor))
        var data = db.repoDao().loadContributors("foo", "bar")
        MatcherAssert.assertThat(getValue(data).size, CoreMatchers.`is`(1))

        val update = TestUtil.createRepo("foo", "bar", "desc")
        db.repoDao().insert(update)
        data = db.repoDao().loadContributors("foo", "bar")
        MatcherAssert.assertThat(getValue(data).size, CoreMatchers.`is`(1))
    }*/
}