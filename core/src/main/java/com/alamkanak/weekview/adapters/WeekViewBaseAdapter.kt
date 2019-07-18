package com.alamkanak.weekview.adapters

import com.alamkanak.weekview.DrawingContext
import com.alamkanak.weekview.EventCache
import com.alamkanak.weekview.EventChipsProvider
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewEvent
import java.util.Calendar

internal class WeekViewBaseAdapter<T>(
    private val view: WeekView<T>,
    private val eventCache: EventCache<T>,
    private val eventChipsProvider: EventChipsProvider<T>,
    private val drawingContext: DrawingContext
) : WeekView.Adapter<T> {

    private val dateRange: List<Calendar>
        get() = drawingContext.dateRangeWithStartPixels.map { it.first }

    override fun submit(items: List<WeekViewDisplayable<T>>) {
        val events = items.map { it.toWeekViewEvent() }
        eventChipsProvider.storeEventsAndCalculateEventChipPositions(events)
        invalidateIfVisibleEventsChanged(events)
    }

    private fun invalidateIfVisibleEventsChanged(events: List<WeekViewEvent<T>>) {
        val allDayEvents = eventCache.getAllDayEventsInRange(dateRange)
        val singleEvents = eventCache.getSingleEventsInRange(dateRange)
        val (newAllDayEvents, newSingleEvents) = events.partition { it.isAllDay }

        // Check is all-day events changed
        var didChangeAllDayEvent = false
        for (oldEvent in allDayEvents) {
            didChangeAllDayEvent = newAllDayEvents.any { it != oldEvent }
            if (didChangeAllDayEvent) {
                break
            }
        }

        // Check if single events changed
        var didChangeSingleEvent = false
        for (oldEvent in singleEvents) {
            didChangeSingleEvent = newSingleEvents.any { it != oldEvent }
            if (didChangeSingleEvent) {
                break
            }
        }

        val didChangeInCurrentDateRange = didChangeAllDayEvent || didChangeSingleEvent
        if (didChangeInCurrentDateRange) {
            view.invalidate()
        }
    }

}
