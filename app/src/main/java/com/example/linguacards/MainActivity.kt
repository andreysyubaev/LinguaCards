package com.example.linguacards

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val lang = prefs.getString("language", "ru") ?: "ru"

        val appLocale = LocaleListCompat.forLanguageTags(lang)
        AppCompatDelegate.setApplicationLocales(appLocale)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_screen)
    }
}