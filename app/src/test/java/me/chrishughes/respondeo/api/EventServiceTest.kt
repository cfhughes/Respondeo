/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.chrishughes.respondeo.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.GsonBuilder
import me.chrishughes.respondeo.util.LiveDataCallAdapterFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsNull.notNullValue
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import me.chrishughes.respondeo.util.LiveDataTestUtil.getValue
import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.EventDeserializer
import me.chrishughes.respondeo.vo.Member
import me.chrishughes.respondeo.vo.RsvpDeserializer

@RunWith(JUnit4::class)
class EventServiceTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: EventService

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()

        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(Event::class.java, EventDeserializer())
        gsonBuilder.registerTypeAdapter(Member::class.java, RsvpDeserializer())

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(EventService::class.java)


    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getUpcomingEvents() {
        enqueueResponse("events-calendar.json")
        val yigit = (getValue(service.getUpcomingEvents("yigit","self",90)) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/self/calendar?fields=self&page=90"))

        assertThat<List<Event>>(yigit, notNullValue())
        assertThat(yigit[0].id, `is`("zdxxtnyzdbhb"))
        //assertThat(yigit[0].groupurl, `is`("Young-Adults-ABQ"))
        assertThat(yigit[0].rsvpResponse, `is`("yes"))
    }

    /*@Test
    fun getRepos() {
        enqueueResponse("repos-yigit.json")
        val repos = (getValue(service.getRepos("yigit")) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/users/yigit/repos"))

        assertThat(repos.size, `is`(2))

        val repo = repos[0]
        assertThat(repo.fullName, `is`("yigit/AckMate"))

        val owner = repo.owner
        assertThat(owner, notNullValue())
        assertThat(owner.login, `is`("yigit"))
        assertThat(owner.url, `is`("https://api.github.com/users/yigit"))

        val repo2 = repos[1]
        assertThat(repo2.fullName, `is`("yigit/android-architecture"))
    }

    @Test
    fun getContributors() {
        enqueueResponse("contributors.json")
        val value = getValue(service.getContributors("foo", "bar"))
        val contributors = (value as ApiSuccessResponse).body
        assertThat(contributors.size, `is`(3))
        val yigit = contributors[0]
        assertThat(yigit.login, `is`("yigit"))
        assertThat(yigit.avatarUrl, `is`("https://avatars3.githubusercontent.com/u/89202?v=3"))
        assertThat(yigit.contributions, `is`(291))
        assertThat(contributors[1].login, `is`("guavabot"))
        assertThat(contributors[2].login, `is`("coltin"))
    }

    @Test
    fun search() {
        val next = """<https://api.github.com/search/repositories?q=foo&page=2>; rel="next""""
        val last = """<https://api.github.com/search/repositories?q=foo&page=34>; rel="last""""
        enqueueResponse(
            "search.json", mapOf(
                "link" to "$next,$last"
            )
        )
        val response = getValue(service.searchRepos("foo")) as ApiSuccessResponse

        assertThat(response, notNullValue())
        assertThat(response.body.total, `is`(41))
        assertThat(response.body.items.size, `is`(30))
        assertThat<String>(
            response.links["next"],
            `is`("https://api.github.com/search/repositories?q=foo&page=2")
        )
        assertThat<Int>(response.nextPage, `is`(2))
    }*/

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader
            .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }
}
