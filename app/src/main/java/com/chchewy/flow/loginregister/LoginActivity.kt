package com.chchewy.flow.loginregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.chchewy.flow.MainActivity
import com.chchewy.flow.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUserMetadata
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    // Static val
    companion object {
        const val TAG = "LoginActivity"
        const val RC_SIGN_IN = 1
        const val GoogleBundle = 2
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

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

        // Start activity for Google Sign In
        googleSignInButton.setOnClickListener {
            googleLogin()
        }

        // GoogleSignInOptions with IdToken and Email
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()
    }

    // Verify if user is already logged in
    // If logged in then skip to MainActivity
    // If NOT logged in then app starts at LoginActivity
    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) {
            // If there is a user logged then UID will not be null
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

        // Making sure both Email and Password are not null
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter your Email/Password", Toast.LENGTH_SHORT).show()
            return true
        }

        Log.d(TAG, "Attempt login with email/password: $email/***")

        // Firebase Authentication process
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


    private fun googleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    // Log in with Google process
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // checking request code value
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    // Signing in with Google account using AuthWithGoogle
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id!!)

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // If successful
                    val metadata: FirebaseUserMetadata = auth.currentUser?.metadata!!
                    Log.d(TAG, "signInWithCredential: Success")
                    Log.d(TAG, "last signed in: ${metadata.lastSignInTimestamp}")
                    Log.d(TAG, "creation time: ${metadata.creationTimestamp}")
                    
                    if (metadata.creationTimestamp == metadata.lastSignInTimestamp) {
                        // If user is new then continue to registration page
                        val intent = Intent(this, GoogleLoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If user is NOT new then skip to MainActivity
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                } else {
                    // If unsuccessful, Toast to user
                    Log.w(TAG, "signInWithCredential: Failure", it.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
