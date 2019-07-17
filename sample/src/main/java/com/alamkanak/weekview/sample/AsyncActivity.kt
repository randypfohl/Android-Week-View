package com.alamkanak.weekview.sample

import android.app.ProgressDialog
import android.graphics.RectF
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewDisplayable
import com.alamkanak.weekview.sample.data.FakeEventsApi
import com.alamkanak.weekview.sample.data.model.ApiEvent
import java.util.Calendar
import java.util.Locale

class AsyncViewModel(
    private val eventsApi: FakeEventsApi
) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    init {
        loadEvents()
    }

    private fun loadEvents() {
        _viewState.value = ViewState(isLoading = true)
        eventsApi.fetchEvents {
            _viewState.value = ViewState(events = it)
        }
    }
}

data class ViewState(
    val events: List<ApiEvent> = emptyList(),
    val isLoading: Boolean = false
)

class AsyncActivity : AppCompatActivity() {

    private val events = arrayListOf<WeekViewDisplayable<ApiEvent>>()
    private var weekViewType = TYPE_THREE_DAY_VIEW

    private lateinit var weekView: WeekView<ApiEvent>

    private val viewModel: AsyncViewModel by lazy {
        AsyncViewModel(FakeEventsApi(this))
    }

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this).apply {
            setCancelable(false)
            setMessage("Loading events ...")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        weekView = findViewById(R.id.weekView)
        weekView.setupWithDefaultAdapter()

        weekView.setOnEventClickListener(this::onEventClick)
        weekView.setOnEventLongPressListener(this::onEventLongPress)
        weekView.setOnEmptyViewLongPressListener(this::onEmptyViewLongPress)

        viewModel.viewState.observe(this, Observer {
            if (it.isLoading) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }

            weekView.submit(it.events)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_today -> {
                weekView.goToToday()
                true
            }
            R.id.action_day_view -> {
                openDayView(item)
                true
            }
            R.id.action_three_day_view -> {
                openThreeDayView(item)
                true
            }
            R.id.action_week_view -> {
                openWeekView(item)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openDayView(item: MenuItem) {
        if (weekViewType == TYPE_DAY_VIEW) {
            return
        }

        item.isChecked = item.isChecked.not()
        weekViewType = TYPE_DAY_VIEW
        weekView.numberOfVisibleDays = 1
    }

    private fun openThreeDayView(item: MenuItem) {
        if (weekViewType == TYPE_THREE_DAY_VIEW) {
            return
        }

        item.isChecked = item.isChecked.not()
        weekViewType = TYPE_THREE_DAY_VIEW
        weekView.numberOfVisibleDays = 3
    }

    private fun openWeekView(item: MenuItem) {
        if (weekViewType == TYPE_WEEK_VIEW) {
            return
        }

        item.isChecked = item.isChecked.not()
        weekViewType = TYPE_WEEK_VIEW
        weekView.numberOfVisibleDays = 7
    }

    private fun getEventTitle(time: Calendar): String {
        val hour = time.get(Calendar.HOUR_OF_DAY)
        val minute = time.get(Calendar.MINUTE)
        val month = time.get(Calendar.MONTH) + 1
        val dayOfMonth = time.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.getDefault(), "Event of %02d:%02d %s/%d", hour, minute, month, dayOfMonth)
    }

    private fun onEventClick(event: ApiEvent, eventRect: RectF) {
        Toast.makeText(this, "Removing ${event.name} ...", Toast.LENGTH_SHORT).show()
        events.remove(event)
        weekView.notifyDataSetChanged()
    }

    private fun onEventLongPress(event: ApiEvent, eventRect: RectF) {
        Toast.makeText(this, "Long pressed event: " + event.name, Toast.LENGTH_SHORT).show()
    }

    private fun onEmptyViewLongPress(time: Calendar) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TYPE_DAY_VIEW = 1
        private const val TYPE_THREE_DAY_VIEW = 2
        private const val TYPE_WEEK_VIEW = 3
    }

}
