package me.chrishughes.respondeo.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import me.chrishughes.respondeo.repository.EventRepository
import me.chrishughes.respondeo.util.AbsentLiveData
import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.Member
import me.chrishughes.respondeo.vo.Resource
import javax.inject.Inject

class EventViewModel @Inject constructor(repository: EventRepository) : ViewModel() {
    private val _eventId: MutableLiveData<EventId> = MutableLiveData()
    val eventId: LiveData<EventId>
        get() = _eventId
    val event: LiveData<Resource<Event>>? = Transformations
        .switchMap(_eventId) {input ->
            if (input == null){
                AbsentLiveData.create()
            } else {
                repository.loadEvent(
                    id = input.id,
                    urlName = input.groupName)
            }
        }
    val rsvps: LiveData<Resource<List<Member>>> = Transformations
        .switchMap(_eventId) {input ->
            input.ifExists { groupName, id ->
                repository.loadRsvps(groupName, id)
            }

        }

    fun setId(id: String, groupName: String) {
        val update = EventId(id = id, groupName = groupName)
        if (_eventId.value == update) {
            return
        }
        _eventId.value = update
    }

    fun retry() {
        _eventId.value?.let {
            _eventId.value = it
        }
    }

    data class EventId(val groupName: String, val id: String) {
        fun <T> ifExists(f: (String, String) -> LiveData<T>): LiveData<T> {
            return if (groupName.isBlank() || id.isBlank()) {
                AbsentLiveData.create()
            } else {
                f(groupName, id)
            }
        }
    }
}