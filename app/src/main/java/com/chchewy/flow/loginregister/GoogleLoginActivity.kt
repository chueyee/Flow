package com.chchewy.flow.loginregister

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.chchewy.flow.MainActivity
import com.chchewy.flow.R
import com.chchewy.flow.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_google_login.*
import java.util.*

class GoogleLoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "GoogleLoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_login)

        register_button.setOnClickListener {
            performRegister()
        }

        cancel_register_button.setOnClickListener {
            finish()
        }

        selectpicture_register_imageview.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }


    }

    private fun performRegister() {
        val ref = FirebaseAuth.getInstance()
        val currentUser = ref.currentUser

        val currentUserEmail = currentUser?.email
        var goal = goal_register_edittext.text.toString()

        if (goal.isEmpty())
            goal = "0"

        Log.d(TAG, "Email = $currentUserEmail")

        uploadImageToFirebaseStorage()

    }

    private var selectedPhotoUri: Uri? = null

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d(TAG, "File Location: $it")

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
            User(uid, FirebaseAuth.getInstance().currentUser?.email.toString(), goal_register_edittext.text.toString().toFloat(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully saved user to FireBase Database")

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
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
