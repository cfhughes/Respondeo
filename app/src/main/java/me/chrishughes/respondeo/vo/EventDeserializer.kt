package me.chrishughes.respondeo.vo

import com.google.gson.*
import java.lang.reflect.Type


internal class EventDeserializer : JsonDeserializer<Event> {
    @Throws(JsonParseException::class)
    override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): Event {
        val content = je.asJsonObject
        val message: Event = Gson().fromJson(je, Event::class.java)
        val groupurl = content.get("group").asJsonObject.get("urlname")
        message.groupurl = groupurl.asString
        if (content.get("self").asJsonObject.has("rsvp")) {
            message.rsvpResponse = content.get("self").asJsonObject.get("rsvp").asJsonObject.get("response").asString
        }else{
            message.rsvpResponse = "no"
        }
        message.rsvpEnabled = true
        return message
    }
}