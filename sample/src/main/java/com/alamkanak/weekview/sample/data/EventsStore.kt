package com.alamkanak.weekview.sample.data

import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.sample.data.model.Event

class EventsStore(
    private val database: EventsDatabase
) {

    fun fetchEvents(
        onSuccess: (List<WeekViewDisplayable<Event>>) -> Unit
    ) {
        val events = database.getAll()
        onSuccess(events)
    }

    fun fetchEvents(
        onSuccessListener: OnSuccessListener
    ) {
        val events = database.getAll()
        onSuccessListener.onSuccess(events)
    }

    @FunctionalInterface
    interface OnSuccessListener {
        fun onSuccess(events: List<WeekViewDisplayable<Event>>)
    }

}
