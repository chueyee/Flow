package com.chchewy.flow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import com.chchewy.flow.models.PlannedEvent
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.current_date_tab_row.view.*

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        current_date_recycler_view.adapter = adapterCurrentDate
        current_date_recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        current_week_recycler_view.adapter = adapterCurrentWeek
        current_week_recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        /*adapter.setOnItemClickListener {item, view ->
            Log.d(TAG, "adapter clicked!")

            val row = item as CurrentDateRow
        }*/

        listenForDummyData()
    }

    private fun listenForDummyData() {
        adapterCurrentDate.add(EventItemCurrentDate())
        adapterCurrentDate.add(EventItemCurrentDate())
        adapterCurrentDate.add(EventItemCurrentDate())
        adapterCurrentDate.add(EventItemCurrentDate())
        adapterCurrentDate.add(EventItemCurrentDate())
        adapterCurrentDate.add(EventItemCurrentDate())

        adapterCurrentWeek.add(EventItemCurrentWeek())
        adapterCurrentWeek.add(EventItemCurrentWeek())
        adapterCurrentWeek.add(EventItemCurrentWeek())
        adapterCurrentWeek.add(EventItemCurrentWeek())
        adapterCurrentWeek.add(EventItemCurrentWeek())
        adapterCurrentWeek.add(EventItemCurrentWeek())
    }

    val adapterCurrentDate = GroupAdapter<ViewHolder>()
    val adapterCurrentWeek = GroupAdapter<ViewHolder>()
}

class EventItemCurrentDate(): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.current_date_tab_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        //viewHolder.itemView.event_text_view_row.text = eventItem.text
    }

}

class EventItemCurrentWeek(): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
    }

    override fun getLayout(): Int {
        return R.layout.current_week_tab_row
    }
}
