package com.example.linguacards

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class result : AppCompatActivity() {
    private lateinit var tvTotalCards: TextView
    private lateinit var tvCardsKnown: TextView
    private lateinit var tvCardsDontKnown: TextView
    private lateinit var bFinish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        val toolbar = findViewById<MaterialToolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)

        tvTotalCards = findViewById(R.id.tvTotalCards)
        tvCardsKnown = findViewById(R.id.tvCardsKnown)
        tvCardsDontKnown = findViewById(R.id.tvCardsDontKnown)
        bFinish = findViewById(R.id.bFinish)

        val total = intent.getIntExtra("TOTAL_CARDS", 0)
        val known = intent.getIntExtra("CARDS_KNOWN", 0)
        val dontKnow = intent.getIntExtra("CARDS_DONT_KNOWN", 0)

        tvTotalCards.text = total.toString()
        tvCardsKnown.text = known.toString()
        tvCardsDontKnown.text = dontKnow.toString()

        bFinish.setOnClickListener {
            finish()
        }
    }
}