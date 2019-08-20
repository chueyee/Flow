package com.chchewy.flow.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.chchewy.flow.MainActivity
import com.chchewy.flow.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    // Static val
    companion object {
        const val TAG = "LoginActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        verifyUserIsLoggedIn()

        // Begin credentials authentication for login
        login_button.setOnClickListener {
            if (performLogin()) return@setOnClickListener
        }

        // Start activity for registration
        register_login_textview.setOnClickListener {
            performRegister()
        }


    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    // Registration activity
    private fun performRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }

    // Login authentication
    private fun performLogin(): Boolean {
        val email = email_login_edittext.text.toString()
        val password = password_login_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your Email/Password", Toast.LENGTH_SHORT).show()
            return true
        }

        Log.d(TAG, "Attempt login with email/password: $email/***")

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(TAG, "Successfully logged in as user: ${it.result?.user?.email}")

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to login as: ${it.message}")

                Toast.makeText(this, "Failed to login as ${it.message}", Toast.LENGTH_SHORT).show()
            }
        return false


    }
}
