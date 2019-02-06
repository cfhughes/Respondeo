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

package me.chrishughes.respondeo.util

import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.Member
import me.chrishughes.respondeo.vo.MemberRsvp

object TestUtil {

    fun createEvent(id: String, name: String, description: String) = Event(
        name = name,
        description = description,
        groupurl = "SomeGroup",
        time = "12:30",
        id = id,
        rsvpResponse = "yes"
    )

    fun createMember(id: Long) = Member(
        id = id,
        memberName = "Bob $id",
        photoLink = "link/$id"
    )

    /*fun createRepos(count: Int, owner: String, name: String, description: String): List<Repo> {
        return (0 until count).map {
            createRepo(
                owner = owner + it,
                name = name + it,
                description = description + it
            )
        }
    }

    fun createRepo(owner: String, name: String, description: String) = createRepo(
        id = Repo.UNKNOWN_ID,
        owner = owner,
        name = name,
        description = description
    )

    fun createRepo(id: Int, owner: String, name: String, description: String) = Repo(
        id = id,
        name = name,
        fullName = "$owner/$name",
        description = description,
        owner = Repo.Owner(owner, null),
        stars = 3
    )
    */

    fun createMemberRsvp(event: Event, member: Member) = MemberRsvp(
        eventId = event.id,
        memberId = member.id
    )
}
