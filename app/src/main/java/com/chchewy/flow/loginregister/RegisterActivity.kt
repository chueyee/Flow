package com.chchewy.flow.loginregister

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.chchewy.flow.MainActivity
import com.chchewy.flow.R
import com.chchewy.flow.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    companion object {
        const val TAG = "RegisterActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button.setOnClickListener {
            performRegister()
        }

        cancel_register_button.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        selectpicture_register_imageview.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private fun performRegister() {
        val email = email_register_edittext.text.toString()
        val password = password_register_edittext.text.toString()
        var goal = goal_register_edittext.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter a valid email/password", Toast.LENGTH_SHORT).show()
            return
        }

        if (goal.isEmpty()) {
            goal = "0"
        }

        Log.d(TAG, "Email is: $email")
        Log.d(TAG, "Password is: $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d(TAG, "Successfully created user with uid: ${it.result?.user?.uid}")

                uploadImageToFirebaseStorage()

            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var selectedPhotoUri: Uri? = null

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("RegisterActivity", "File Location: $it")

                    saveUserToDatabase(it.toString())
                }
            }
            .addOnFailureListener{
                Log.d(TAG, "Unsuccessful: $it")
            }
    }

    private fun saveUserToDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")

        val user =
            User(uid, email_register_edittext.text.toString(), goal_register_edittext.text.toString().toFloat(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully saved user to Firebase Database")

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to save user: ${it.message}")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the image was
            Log.d("RegisterActivity", "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectpicture_register_imageview.setImageBitmap(bitmap)
        }
    }
}
