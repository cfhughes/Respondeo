package me.chrishughes.respondeo.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import me.chrishughes.respondeo.repository.EventRepository
import me.chrishughes.respondeo.vo.Event
import me.chrishughes.respondeo.vo.Resource
import javax.inject.Inject

class CalendarViewModel @Inject constructor(eventRepository: EventRepository) : ViewModel() {

    val results: LiveData<Resource<List<Event>>> = eventRepository.loadEvents()



}