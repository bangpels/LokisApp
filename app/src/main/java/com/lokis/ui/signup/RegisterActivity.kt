package com.lokis.ui.signup

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.lokis.MainActivity
import com.lokis.R
import com.lokis.User
import com.lokis.databinding.ActivityRegisterBinding
import com.lokis.ui.login.LoginActivity
import com.lokis.utils.MyGetLoc
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var  auth: FirebaseAuth
    private val database = FirebaseFirestore.getInstance()

    var fullname: String = ""
    var email: String = ""
    var password: String = ""
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnSignUp: Button = findViewById(R.id.btnSignUp)
        val btnLogin: TextView = findViewById(R.id.btnToLogin)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)

        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        btnSignUp.setOnClickListener {
            val email: String = etEmail.text.toString().trim()
            val password: String = etPassword.text.toString().trim()


            if (email.isEmpty()){
                etEmail.error = "Email harus diisi!"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etEmail.error = "Email tidak valid!"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6){
                etPassword.error = "Password harus diisi dan lebih dari 6 karakter!"
                etPassword.requestFocus()
                return@setOnClickListener
            }
            registerUser(email, password)
        }
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    if (imageUri == null) {
                        createUserWithoutImage()
                    } else {
                        createUserWithImage()
                    }
                    Intent(this@RegisterActivity, com.lokis.MainActivity::class.java).also { intent->
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else{
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser!=null){
            Intent(this@RegisterActivity, com.lokis.MainActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        }
    }

    private fun createUserWithoutImage() {
        val userid = auth.currentUser!!.uid
        val items = HashMap<String, Any>()
        items["email"] = email
        items["userName"] = fullname
        items["userID"] = userid
        items["profilePictureURL"] = ""
        items["active"] = true
        saveUserToDatabase(auth.currentUser!!, items)
    }

    private fun createUserWithImage() {
        val data = FirebaseStorage.getInstance().reference
        val photoRef = data.child("images/" + auth.currentUser!!.uid + ".png")
        photoRef.putFile(this.imageUri!!).addOnProgressListener {
        }.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            photoRef.downloadUrl
        }.addOnSuccessListener { downloadUri ->

            val userId = auth.currentUser!!.uid
            val items = HashMap<String, Any>()
            items["email"] = email
            items["userName"] = fullname
            items["userID"] = userId
            items["profilePictureURL"] = downloadUri.toString()
            items["active"] = true
            saveUserToDatabase(auth.currentUser!!, items)
        }
    }

    private fun saveUserToDatabase(user: FirebaseUser, items: HashMap<String, Any>) {

        database.collection("users").document(user.uid).set(items)
            .addOnSuccessListener {
                val userModel = User()
                userModel.userID = user.uid
                userModel.email = items["email"].toString()
                userModel.userName = items["userName"].toString()
                userModel.profilePictureURL = items["profilePictureURL"].toString()
                userModel.active = true
                MyGetLoc.currentUser = userModel
                Log.d("SignUp state", "save user:success")
            }
    }
}