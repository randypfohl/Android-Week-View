package com.alamkanak.weekview.adapters

import com.alamkanak.weekview.DrawingContext
import com.alamkanak.weekview.EventCache
import com.alamkanak.weekview.EventChipsProvider
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewEvent
import java.util.Calendar

internal class WeekViewBaseAdapter<T> : WeekView.Adapter<T> {

    private lateinit var view: WeekView<T>
    private lateinit var eventCache: EventCache<T>
    private lateinit var eventChipsProvider: EventChipsProvider<T>
    private lateinit var drawingContext: DrawingContext

    private val dateRange: List<Calendar>
        get() = drawingContext.dateRangeWithStartPixels.map { it.first }

    internal fun init(
        view: WeekView<T>,
        eventCache: EventCache<T>,
        eventChipsProvider: EventChipsProvider<T>,
        drawingContext: DrawingContext
    ) {
        this.view = view
        this.eventCache = eventCache
        this.eventChipsProvider = eventChipsProvider
        this.drawingContext = drawingContext
    }

    override fun submit(items: List<WeekViewDisplayable<T>>) {
        val events = items.map { it.toWeekViewEvent() }
        eventCache.storeAllEvents(events)
        eventChipsProvider.calculateEventChipPositions()
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
