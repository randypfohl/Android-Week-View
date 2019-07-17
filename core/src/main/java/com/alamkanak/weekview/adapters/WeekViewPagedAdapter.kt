package com.alamkanak.weekview.adapters

import com.alamkanak.weekview.EventChipsProvider
import com.alamkanak.weekview.FetchPeriods
import com.alamkanak.weekview.OnMonthChangeListener
import com.alamkanak.weekview.Period
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.WeekViewViewState
import java.util.Calendar

internal class WeekViewPagedAdapter<T> : WeekView.Adapter<T> {

    private lateinit var view: WeekView<T>
    private lateinit var viewState: WeekViewViewState
    private lateinit var eventChipsProvider: EventChipsProvider<T>

    private val firstVisibleDate: Calendar
        get() = checkNotNull(viewState.firstVisibleDate)

    internal fun init(
        view: WeekView<T>,
        viewState: WeekViewViewState,
        eventChipsProvider: EventChipsProvider<T>
    ) {
        this.view = view
        this.viewState = viewState
        this.eventChipsProvider = eventChipsProvider
    }

    override fun submit(items: List<WeekViewDisplayable<T>>) {
        val events = items.map { it.toWeekViewEvent() }
        val periodsWithEvents = events.groupBy { Period.fromDate(it.startTime) }
        eventChipsProvider.storeEventsAndCalculateEventChipPositions(periodsWithEvents)
        view.invalidate()
    }

    internal fun loadInitial() {
        val fetchPeriods = FetchPeriods.create(firstVisibleDate)
        val start = fetchPeriods.previous.startDate
        val end = fetchPeriods.next.endDate
        view.onMonthChangeListener?.onMonthChange(start, end)

    }

}

@FunctionalInterface
interface OnLoadMoreListener {
    fun onLoadMore(startDate: Calendar, endDate: Calendar)
}

// TODO Refactor this
var <T> WeekView<T>.onLoadMoreListener: OnLoadMoreListener?
    get() = object : OnLoadMoreListener {
        override fun onLoadMore(
            startDate: Calendar,
            endDate: Calendar
        ) {
            onMonthChangeListener?.onMonthChange(startDate, endDate)
        }
    }
    set(value) {
        onMonthChangeListener = object : OnMonthChangeListener<T> {
            override fun onMonthChange(
                startDate: Calendar,
                endDate: Calendar
            ): List<WeekViewDisplayable<T>> {
                value?.onLoadMore(startDate, endDate)
                return emptyList()
            }
        }
    }

fun <T> WeekView<T>.setOnLoadMoreListener(
    block: (startDate: Calendar, endDate: Calendar) -> Unit
) {
    onMonthChangeListener = object : OnMonthChangeListener<T> {
        override fun onMonthChange(
            startDate: Calendar,
            endDate: Calendar
        ): List<WeekViewDisplayable<T>> {
            block(startDate, endDate)
            return emptyList()
        }
    }
}
