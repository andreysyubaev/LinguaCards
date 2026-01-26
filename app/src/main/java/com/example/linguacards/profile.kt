package com.example.linguacards

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.linguacards.data.model.AppDataBase
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class profile : AppCompatActivity() {

    private lateinit var ivProfilePhoto: ImageView
    private lateinit var ibChangePhoto: ImageButton
    private lateinit var etUsername: TextView
    private lateinit var etEmail: TextView
    private lateinit var etPassword: TextView
    private lateinit var etCreatedAt: TextView
    private lateinit var bEdit: Button
    private lateinit var bLogout: Button
    private lateinit var bDeleteAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        if (prefs.getInt("user_id", -1) == -1) {
            startActivity(Intent(this, login::class.java))
            finish()
            return
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        ivProfilePhoto = findViewById(R.id.ivProfilePhoto)
//        ibChangePhoto = findViewById(R.id.ibChangePhoto)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etCreatedAt = findViewById(R.id.etCreatedAt)
        bEdit = findViewById(R.id.bEdit)
        bLogout = findViewById(R.id.bLogout)
        bDeleteAccount = findViewById(R.id.bDeleteAccount)

        bEdit.setOnClickListener {
            val intent = Intent(this, editProfile::class.java)
            intent.putExtra("username", etUsername.text.toString())
            intent.putExtra("email", etEmail.text.toString())
            startActivity(intent)
        }

        bLogout.setOnClickListener {
            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
            prefs.edit().clear().apply()

            val intent = Intent(this, login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        bDeleteAccount.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Delete account?")
                .setMessage("This action cannot be undone")
                .setPositiveButton("Delete") { _, _ ->
                    lifecycleScope.launch {
                        val db = AppDataBase.getDatabase(this@profile)
                        db.userDao().deleteById(getUserId())

                        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                        prefs.edit().clear().apply()

                        val intent = Intent(this@profile, login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        loadUserProfile()
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = getUserId()
        if (userId == -1) finish()

        lifecycleScope.launch {
            val db = AppDataBase.getDatabase(this@profile)
            val user = db.userDao().getById(userId) ?: run {
                toast("User not found")
                finish()
                return@launch
            }

            etUsername.text = user.username
            etEmail.text = user.email
            etPassword.text = user.password

            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            etCreatedAt.text = sdf.format(user.createdAt)
        }
    }

    private fun getUserId(): Int {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        return prefs.getInt("user_id", -1)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}