package com.alamkanak.weekview

import java.util.Calendar

internal data class FetchPeriods(
    val previous: Period,
    val current: Period,
    val next: Period
) {

    fun toList() = listOf(previous, current, next)

    internal companion object {

        fun from(periods: List<Period>): FetchPeriods {
            check(periods.size == 3)
            return FetchPeriods(periods[0], periods[1], periods[2])
        }

        fun create(firstVisibleDay: Calendar): FetchPeriods {
            val current = Period.fromDate(firstVisibleDay)
            return FetchPeriods(current.previous, current, current.next)
        }

    }

}

internal data class Period(val month: Int, val year: Int) {

    val previous: Period
        get() {
            val year = if (month == Calendar.JANUARY) year - 1 else year
            val month = if (month == Calendar.JANUARY) Calendar.DECEMBER else month - 1
            return Period(month, year)
        }

    val next: Period
        get() {
            val year = if (month == Calendar.DECEMBER) year + 1 else year
            val month = if (month == Calendar.DECEMBER) Calendar.JANUARY else month + 1
            return Period(month, year)
        }

    val startDate: Calendar
        get() = today().withMonth(month).withDayOfMonth(1).withTimeAtStartOfPeriod(hour = 0)

    val endDate: Calendar
        get() {
            val date = today().withMonth(month)
            val maxDays = date.getActualMaximum(Calendar.DAY_OF_MONTH)
            return date.withDayOfMonth(maxDays).withTimeAtEndOfPeriod(hour = 24)
        }

    internal companion object {

        fun fromDate(date: Calendar): Period {
            val month = date.month
            val year = date.year
            return Period(month, year)
        }

    }

}
