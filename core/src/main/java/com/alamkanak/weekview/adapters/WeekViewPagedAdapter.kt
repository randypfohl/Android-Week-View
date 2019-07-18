package com.alamkanak.weekview.adapters

import com.alamkanak.weekview.EventChipsProvider
import com.alamkanak.weekview.FetchPeriods
import com.alamkanak.weekview.Period
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewViewState

internal class WeekViewPagedAdapter<T>(
    private val view: WeekView<T>,
    private val viewState: WeekViewViewState,
    private val eventChipsProvider: EventChipsProvider<T>
) : WeekView.Adapter<T> {

    override fun submit(items: List<WeekViewDisplayable<T>>) {
        val events = items.map { it.toWeekViewEvent() }
        val periodsWithEvents = events.groupBy { Period.fromDate(it.startTime) }
        eventChipsProvider.storeEventsAndCalculateEventChipPositions(periodsWithEvents)
        eventChipsProvider.eventLoader?.completeFetching(periodsWithEvents.keys)
        view.invalidate()
    }

    internal fun loadInitial() {
        val firstVisibleDate = checkNotNull(viewState.firstVisibleDate)
        val fetchPeriods = FetchPeriods.create(firstVisibleDate)
        val start = fetchPeriods.previous.startDate
        val end = fetchPeriods.next.endDate
        eventChipsProvider.eventLoader?.onLoadMoreListener?.onLoadMore(start, end)
    }

}
