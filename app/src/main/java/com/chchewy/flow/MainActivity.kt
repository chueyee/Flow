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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    // Static val
    companion object {
        const val TAG = "MainActivity"
    }

    // Test variables
    var hour : Long = 3600
    var setGoal : Long = 100
    var calculatedStatus = 0

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

        // Getting the current date
        val currentCalendar = Calendar.getInstance()
        var currentDay = currentCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())

        progressBar = findViewById(R.id.home_progress_bar)
        progressAsText = findViewById(R.id.home_progress_textview)

        current_day_text_view.text = currentDay

        // Setting up variables to access current user data by creating variable with User's ID
        // and using it to retrieve the account's data
        var user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid.toString()

        // Creating reference variable to access the Firebase Database to retrieve user's data
        val ref = FirebaseDatabase.getInstance().getReference("/user").child(userId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val photoUrl = p0.child("profileImageUrl").value

                Picasso.get().load(photoUrl.toString()).into(profile_image_button)
                Log.d(TAG, photoUrl.toString())
                Log.d(TAG, userId)

                if (photoUrl == null)
                    Log.d(TAG, "Photo URL is null")
            }
        })

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val goal = p0.child("goal").value as Long
                setGoal = goal
                Log.d(TAG, goal.toString())


            }

        })
        // Setting up click listener for ProgressBar
        // Click to start/pause
        home_progress_bar.setOnClickListener {
            // If false then starts the timer and keeps track of time elapsed while updating the Progress Bar
            // If true then stops timer and saves progress before removing callbacks to maintain progress of Progress Bar
            if (flag) {
                progressSaved = calculatedStatus
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
        //
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

        profile_image_button.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    // Runnable Object for Handler to update Progress Bar
    private var runnable: Runnable = object : Runnable {

        override fun run() {
            val df = DecimalFormat("#")

            millisecondTime = SystemClock.uptimeMillis() - startTime
            updateTime = timeBuff + millisecondTime
            seconds = (updateTime / 1000).toInt()
            milliSeconds = (updateTime % 1000).toInt()

            progressStatus = ((progressSaved.toDouble() + seconds) / hour) * 100
            calculatedStatus = (progressStatus/setGoal).toInt()
            progressBar?.progress = calculatedStatus

            var formattedValue = df.format(calculatedStatus)
            progressAsText?.text = formattedValue.plus(percentString)

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
