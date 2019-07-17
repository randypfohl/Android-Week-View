package com.alamkanak.weekview.sample.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.sample.data.model.Event
import java.util.Calendar

class ReactiveEventsStore(
    private val database: EventsDatabase
) {

    private val _events = MutableLiveData<List<WeekViewDisplayable<Event>>>()
    val events: LiveData<List<WeekViewDisplayable<Event>>> = _events

    fun fetchEvents(
        startDate: Calendar,
        endDate: Calendar
    ) {
        _events.value = database.getEventsInRange(startDate, endDate)
    }

}
