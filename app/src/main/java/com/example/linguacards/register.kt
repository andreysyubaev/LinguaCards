package com.example.linguacards

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.User


class register : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var ibVisiblePassword: ImageButton
    private lateinit var ibVisibleConfirmPassword: ImageButton
    private lateinit var bCreateNewAccount: Button

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
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
        bCreateNewAccount = findViewById(R.id.bCreateNewAccount)

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

        bCreateNewAccount.setOnClickListener {
            if (!validateInput()) return@setOnClickListener

            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            lifecycleScope.launch {
                val db = AppDataBase.getDatabase(this@register)
                val userDao = db.userDao()

                if (userDao.getByUsername(username) != null) {
                    toast(getString(R.string.username_already_exists))
                    return@launch
                }

                if (userDao.getByEmail(email) != null) {
                    toast(getString(R.string.email_already_registered))
                    return@launch
                }

                val user = User(
                    username = username,
                    email = email,
                    password = password
                )

                userDao.insert(user)

                toast(getString(R.string.account_created))
                finish()
            }
        }
    }

    private fun validateInput(): Boolean {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (username.isEmpty() || email.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty()
        ) {
            toast(getString(R.string.fill_both_fields))
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

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}