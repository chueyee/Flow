package com.chchewy.flow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    // Static val/var
    companion object {
        const val TAG = "MainActivity"
    }

    // Test variables
    var testGoal = 200
    var newProgress: Int = 0

    var progressBar: ProgressBar? = null
    var progressStatus: Double = 0.0
    var progressSaved = 0
    var progressAsText: TextView? = null
    var handler = Handler()
    var flag: Boolean = false
    val percentString: String = "%"

    var millisecondTime: Long = 0
    var startTime: Long = 0
    var timeBuff: Long = 0
    var updateTime = 0L

    var seconds: Int = 0
    var milliSeconds: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        progressBar = findViewById<ProgressBar>(R.id.home_progress_bar)
        progressAsText = findViewById<TextView>(R.id.home_progress_textview)

        // Setting up click listener for ProgressBar
        // Click to start/pause
        home_progress_bar.setOnClickListener {
            // If false then starts the timer and keeps track of time elapsed while updating the Progress Bar
            // If true then stops timer and saves progress before removing callbacks to maintain progress of Progress Bar
            if (flag) {
                progressSaved = progressStatus.toInt()
                progressBar?.progress = progressSaved
                progressAsText?.text = progressSaved.toString().plus(percentString)
                handler.removeCallbacks(runnable)
                flag = false
            } else {
                Thread(Runnable {
                    startTime = SystemClock.uptimeMillis()
                    handler.postDelayed(runnable, 0)
                    flag = true
                }).start()
                flag = true
            }

        }

        // Setting up the view for each individual Event Items in the RecyclerView
        current_date_recycler_view.adapter = adapterCurrentDate
        current_date_recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // Click functions of Event Items in the RecyclerView
        adapterCurrentDate.setOnItemClickListener {item, view ->
            Log.d(TAG, "date adapter clicked!")

            val intent = Intent(this, EventActivity::class.java)
            startActivity(intent)

        }

        // Creating dummy items for the RecyclerView to test functionality
        listenForDummyData()
    }

    // Runnable Object for Handler to update Progress Bar
    private var runnable: Runnable = object : Runnable {

        override fun run() {

            millisecondTime = SystemClock.uptimeMillis() - startTime
            updateTime = timeBuff + millisecondTime
            seconds = (updateTime / 1000).toInt()
            milliSeconds = (updateTime % 1000).toInt()

            progressStatus = ((progressSaved.toDouble() + seconds)/testGoal)*100
            progressBar?.progress = progressStatus.toInt()
            progressAsText?.text = progressStatus.toString().plus(percentString)

            handler.postDelayed(this, 0)
        }
    }


    // Dummy items
    private fun listenForDummyData() {
        adapterCurrentDate.add(EventItemCurrentDate())
        adapterCurrentDate.add(EventItemCurrentDate())
        adapterCurrentDate.add(EventItemCurrentDate())
        adapterCurrentDate.add(EventItemCurrentDate())
        adapterCurrentDate.add(EventItemCurrentDate())
        adapterCurrentDate.add(EventItemCurrentDate())
    }

    // Adapters for RecyclerView
    private val adapterCurrentDate = GroupAdapter<ViewHolder>()

}

// Binding layout and functionality of each Event Item in each of the RecyclerViews
class EventItemCurrentDate: Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.current_date_tab_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
    }

}
