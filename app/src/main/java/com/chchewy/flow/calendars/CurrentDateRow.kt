package com.chchewy.flow.calendars

import com.chchewy.flow.R
import com.chchewy.flow.models.PlannedEvent
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.current_date_tab_row.view.*

class CurrentDateRow(val plannedEvent: PlannedEvent): Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.current_date_tab_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.current_date_event_text_view_row.text = plannedEvent.text

    }
}