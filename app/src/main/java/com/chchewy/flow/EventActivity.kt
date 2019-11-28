package com.chchewy.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chchewy.flow.models.PlannedEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        val ref = FirebaseAuth.getInstance().currentUser
        val email = ref?.email

        val textViewResult = textView

/*
        textViewResult.text = email
*/


        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/calendar/v3/users/me/calendarList/$email/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonEventPostApi = retrofit.create(JsonEventPostApi::class.java)

        val call = jsonEventPostApi.getPosts()

        call.enqueue(object : Callback<List<PlannedEvent>> {
            override fun onResponse(call: Call<List<PlannedEvent>>, response: Response<List<PlannedEvent>>) {

                if (!response.isSuccessful) {
                    textViewResult.text = response.code().toString()
                    return
                }

                val events = response.body()

                for (plannedEvent in events!!) {
                    var content = ""
                    content += "ID: ${plannedEvent.id} \n"
                    content += "Message: ${plannedEvent.message} \n\n"

                    textViewResult.append(content)
                }
            }

            override fun onFailure(call: Call<List<PlannedEvent>>, t: Throwable) {
                textViewResult.text = t.message
            }
        })
    }
}
