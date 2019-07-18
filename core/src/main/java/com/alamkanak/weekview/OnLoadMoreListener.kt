package com.alamkanak.weekview

import java.util.Calendar

@FunctionalInterface
interface OnLoadMoreListener {
    fun onLoadMore(startDate: Calendar, endDate: Calendar)
}
