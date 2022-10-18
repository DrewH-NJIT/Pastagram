package com.example.pastagram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseUser


class LoginActivity : AppCompatActivity() {

    lateinit var etUsername: EditText
    lateinit var etPassword: EditText
    lateinit var buttonLogin: Button
    lateinit var buttonSignup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        if (ParseUser.getCurrentUser() != null) {
            goToMainActivity()
        }

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonSignup = findViewById(R.id.buttonSignup)

        buttonLogin.setOnClickListener {

            loginUser(etUsername.text.toString(), etPassword.text.toString())
        }

        buttonSignup.setOnClickListener {

            signupUser(etUsername.text.toString(), etPassword.text.toString())
        }
    }

    private fun loginUser(username: String, password: String) {

        ParseUser.logInInBackground(
            username, password, ({ user, e ->
                if (user != null) {
                    Log.i(TAG, "loginUser: Logged in!")
                    goToMainActivity()
                } else {
                    e.printStackTrace()
                    Toast.makeText(this, "Error Logging in: " + e.message, Toast.LENGTH_SHORT)
                        .show()
                }
            })
        )
    }

    private fun signupUser(username: String, password: String) {
        // Create the ParseUser
        val user = ParseUser()

        // Set fields for the user to be created
        user.setUsername(username)
        user.setPassword(password)

        user.signUpInBackground { e ->
            if (e == null) {
                // Hooray! Let them use the app now.
                Toast.makeText(this, "Added $username to database!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
                Toast.makeText(this, "Error Signing up: " + e.message, Toast.LENGTH_SHORT)
                    .show()

                e.printStackTrace()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        val TAG = "LoginActivity"
    }
}