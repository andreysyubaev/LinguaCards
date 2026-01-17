package com.example.linguacards

import android.content.Intent
import android.os.Bundle
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

class forgotPassword : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var ibVisiblePassword: ImageButton
    private lateinit var ibVisibleConfirmPassword: ImageButton
    private lateinit var bResetPassword: TextView
    private lateinit var db: AppDataBase

    private var isNewPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
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

        db = AppDataBase.getDatabase(this)

        etEmail = findViewById(R.id.etEmail)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        ibVisiblePassword = findViewById(R.id.ibVisiblePassword)
        ibVisibleConfirmPassword = findViewById(R.id.ibVisibleConfirmPassword)
        bResetPassword = findViewById(R.id.bResetPassword)

        ibVisiblePassword.setOnClickListener {
            isNewPasswordVisible = !isNewPasswordVisible

            if (isNewPasswordVisible) {
                etNewPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ibVisiblePassword.setImageResource(R.drawable.eye_off)
            } else {
                etNewPassword.inputType = android.text.InputType.TYPE_CLASS_TEXT or
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

        bResetPassword.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            when {
                email.isBlank() -> {
                    etEmail.error = "Email required"
                    return@setOnClickListener
                }

                newPassword.length < 6 -> {
                    etNewPassword.error = "Password must be at least 6 characters"
                    return@setOnClickListener
                }

                newPassword != confirmPassword -> {
                    etConfirmPassword.error = "Passwords do not match"
                    return@setOnClickListener
                }
            }

            lifecycleScope.launch {

                val user = db.userDao().getByEmail(email)

                if (user == null) {
                    etEmail.error = "User with this email not found"
                    return@launch
                }

                db.userDao().updatePassword(
                    userId = user.id,
                    newPassword = newPassword
                )

                Toast.makeText(
                    this@forgotPassword,
                    "Password updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

    }
}