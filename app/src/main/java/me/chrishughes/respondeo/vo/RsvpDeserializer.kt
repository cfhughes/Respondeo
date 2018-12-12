package me.chrishughes.respondeo.vo

import com.google.gson.*
import java.lang.reflect.Type

internal class RsvpDeserializer : JsonDeserializer<Member> {
    @Throws(JsonParseException::class)
    override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): Member {
        val content = je.asJsonObject
        val member: Member = Gson().fromJson(je, Member::class.java)
        member.id = content.get("member").asJsonObject.get("id").asLong
        member.memberName = content.get("member").asJsonObject.get("name").asString
        if (content.get("member").asJsonObject.get("photo") != null) {
            member.photoLink = content.get("member").asJsonObject.get("photo").asJsonObject.get("photo_link").asString
        }else{
            member.photoLink = ""
        }
        return member
    }
}