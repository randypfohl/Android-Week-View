package com.alamkanak.weekview

/**
 * This class is responsible for loading [WeekViewEvent]s into [WeekView]. It can handle
 * both concrete [WeekViewEvent] objects and [WeekViewDisplayable] objects. The latter is
 * an interface that can be implemented in one's actual data class and handles the conversion to a
 * [WeekViewEvent].
 */
internal class WeekViewEventLoader<T>(
    var onLoadMoreListener: OnLoadMoreListener?
) {

    private val fetchingPeriods = mutableSetOf<Period>()

    fun load(period: Period) {
        val listener = onLoadMoreListener ?: return
        if (fetchingPeriods.contains(period)) {
            return
        }

        fetchingPeriods += period

        val startDate = today()
            .withYear(period.year)
            .withMonth(period.month)
            .withDayOfMonth(1)

        val maxDays = startDate.lengthOfMonth
        val endDate = startDate
            .withDayOfMonth(maxDays)
            .atEndOfDay

        return listener.onLoadMore(startDate, endDate)
    }

    fun completeFetching(periods: Set<Period>) {
        fetchingPeriods.removeAll(periods)
    }

}
