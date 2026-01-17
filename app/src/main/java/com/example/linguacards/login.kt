package com.example.linguacards

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast
import com.example.linguacards.data.model.AppDataBase

class login : AppCompatActivity() {

    private lateinit var etLogin: EditText
    private lateinit var etPassword: EditText
    private lateinit var ibVisiblePassword: ImageButton
    private lateinit var bForgotPassword: TextView
    private lateinit var bLogin: Button
    private lateinit var bRegister: TextView

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val userId = prefs.getInt("user_id", -1)

        if (userId != -1) {
            startActivity(Intent(this, MainScreen::class.java))
            finish()
            return
        }

        etLogin = findViewById(R.id.etLogin)
        etPassword = findViewById(R.id.etPassword)
        ibVisiblePassword = findViewById(R.id.ibVisiblePassword)
        bForgotPassword = findViewById(R.id.bForgotPassword)
        bLogin = findViewById(R.id.bLogin)
        bRegister = findViewById(R.id.bRegister)

        ibVisiblePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ibVisiblePassword.setImageResource(R.drawable.eye_off)
            } else {
                etPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                ibVisiblePassword.setImageResource(R.drawable.eye)
            }
        }

        bForgotPassword.setOnClickListener {
            val intent = Intent(this, forgotPassword::class.java)
            startActivity(intent)
        }

        bLogin.setOnClickListener {
            if (!validateInput()) return@setOnClickListener

            val username = etLogin.text.toString().trim()
            val password = etPassword.text.toString()

            lifecycleScope.launch {
                val db = AppDataBase.getDatabase(this@login)
                val userDao = db.userDao()

                val user = userDao.getByUsername(username)

                if (user == null) {
                    toast("User not found")
                    return@launch
                }

                if (user.password != password) {
                    toast("Wrong password")
                    return@launch
                }

                // сохраняем логин
                saveUserSession(user.id)

                val intent = Intent(this@login, MainScreen::class.java)
                startActivity(intent)
                finish()
            }
        }

        bRegister.setOnClickListener {
            val intent = Intent(this, register::class.java)
            startActivity(intent)
        }
    }

    private fun validateInput(): Boolean {
        if (etLogin.text.isNullOrBlank() || etPassword.text.isNullOrBlank()) {
            toast("Enter login and password")
            return false
        }
        return true
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun saveUserSession(userId: Int) {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        prefs.edit()
            .putInt("user_id", userId)
            .apply()
    }
}