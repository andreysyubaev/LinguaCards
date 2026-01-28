package com.example.linguacards

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.linguacards.data.model.AppDataBase
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class editProfile : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var ibVisiblePassword: ImageButton
    private lateinit var ibVisibleConfirmPassword: ImageButton
    private lateinit var bAccept: Button

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        ibVisiblePassword = findViewById(R.id.ibVisiblePassword)
        ibVisibleConfirmPassword = findViewById(R.id.ibVisibleConfirmPassword)
        bAccept = findViewById(R.id.bAccept)

        etUsername.setText(intent.getStringExtra("username"))
        etEmail.setText(intent.getStringExtra("email"))

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

        ibVisibleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible

            if (isConfirmPasswordVisible) {
                etConfirmPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ibVisibleConfirmPassword.setImageResource(R.drawable.eye_off)
            } else {
                etConfirmPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                ibVisibleConfirmPassword.setImageResource(R.drawable.eye)
            }
        }

        bAccept.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val newUsername = etUsername.text.toString().trim()
        val newEmail = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (newUsername.isBlank() || newEmail.isBlank()) {
            toast(getString(R.string.username_and_email_cannot_be_empty))
            return
        }

        if (password.isBlank() || confirmPassword.isBlank()) {
            toast(getString(R.string.enter_password_to_confirm_changes))
            return
        }

        if (password != confirmPassword) {
            toast(getString(R.string.passwords_do_not_match))
            return
        }
        
        val userId = getUserId()
        if (userId == -1) return

        lifecycleScope.launch {
            val db = AppDataBase.getDatabase(this@editProfile)
            val user = db.userDao().getById(userId)

            if (user == null) {
                toast(getString(R.string.user_not_found))
                return@launch
            }

            if (user.password != password) {
                toast(getString(R.string.incorrect_password))
                return@launch
            }

            db.userDao().update(
                user.copy(
                    username = newUsername,
                    email = newEmail
                )
            )

            toast(getString(R.string.profile_updated))
            finish()
        }
    }


    private fun getUserId(): Int {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        return prefs.getInt("user_id", -1)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun validateInput(): Boolean {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (username.isEmpty() || email.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty()
        ) {
            toast(getString(R.string.fill_in_all_fields))
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast(getString(R.string.invalid_email))
            return false
        }

        if (password.length < 6) {
            toast(getString(R.string.password_must_be_at_least))
            return false
        }

        if (password != confirmPassword) {
            toast(getString(R.string.passwords_do_not_match))
            return false
        }

        return true
    }
}