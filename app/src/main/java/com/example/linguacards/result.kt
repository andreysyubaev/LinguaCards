package com.example.linguacards

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.linguacards.data.model.Card
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class result : AppCompatActivity() {
    private lateinit var tvTotalCards: TextView
    private lateinit var tvCardsKnown: TextView
    private lateinit var tvCardsDontKnown: TextView
    private lateinit var bRestart: Button
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
        bRestart = findViewById(R.id.bRestart)
        bFinish = findViewById(R.id.bFinish)

        val total = intent.getIntExtra("TOTAL_CARDS", 0)
        val known = intent.getIntExtra("CARDS_KNOWN", 0)
        val dontKnow = intent.getIntExtra("CARDS_DONT_KNOWN", 0)

        tvTotalCards.text = total.toString()
        tvCardsKnown.text = known.toString()
        tvCardsDontKnown.text = dontKnow.toString()

        bRestart.setOnClickListener {
            val cards = intent.getParcelableArrayListExtra<Card>("CARDS")

            val restartIntent = Intent(this, fleshcard_game::class.java)

            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.restart_training_this_pack))
                .setMessage(getString(R.string.training_will_start_again))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    restartIntent.putParcelableArrayListExtra("CARDS", cards)
                    restartIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(restartIntent)
                    finish()
                }
                .setNegativeButton(getString(R.string.no), null)
                .show()
        }


        bFinish.setOnClickListener {
            val intent = Intent(this, MainScreen::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}