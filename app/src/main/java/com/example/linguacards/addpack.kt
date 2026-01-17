package com.example.linguacards

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.adapters.CardInPackAdapter
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Card
import com.example.linguacards.data.model.Pack
import com.example.linguacards.data.model.PackCard
import com.example.linguacards.data.model.User
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class addpack : AppCompatActivity() {
    private lateinit var rvCardsInPack: RecyclerView
    private lateinit var cardInPackAdapter: CardInPackAdapter
    private lateinit var etTitle: EditText
    private lateinit var tvCardsCount: TextView
    private lateinit var tvDifficult: TextView
    private lateinit var bAddPack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_pack)

        val fabAddPack: FloatingActionButton = findViewById(R.id.fabAddPack)
        rvCardsInPack = findViewById(R.id.rvCards)
        etTitle = findViewById(R.id.etTitle)
        tvCardsCount = findViewById(R.id.tvCardsCount)
        tvDifficult = findViewById(R.id.tvDifficult)
        bAddPack = findViewById(R.id.bAddPack)

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

        cardInPackAdapter = CardInPackAdapter(mutableListOf()) { currentCards ->
            val count = currentCards.size
            tvCardsCount.text = "Cards count: $count"
            val avgEase = if (currentCards.isNotEmpty()) currentCards.map { it.easeFactor }.average() else 0.0
            tvDifficult.text = "Difficult: %.2f ★".format(avgEase)
        }

        rvCardsInPack.layoutManager = LinearLayoutManager(this)
        rvCardsInPack.adapter = cardInPackAdapter

        val selectedCards = intent.getParcelableArrayListExtra<Card>("selected_cards")
        if (selectedCards != null) {
            cardInPackAdapter.addCards(selectedCards)
        }

        fabAddPack.setOnClickListener {
            val intent = Intent(this, chooseCardForAddInPack::class.java)
            intent.putParcelableArrayListExtra(
                "cards_in_pack",
                ArrayList(cardInPackAdapter.getCards())
            )
            chooseCardsLauncher.launch(intent)
        }

        bAddPack.setOnClickListener {
            val packName = etTitle.text.toString().ifBlank { "New pack" }

            lifecycleScope.launch {
                val db = AppDataBase.getDatabase(this@addpack)

                val userId = getCurrentUserId()
                if (userId == -1) {
                    finish()
                    return@launch
                }

                val packId = withContext(Dispatchers.IO) {
                    db.packDao().insert(
                        Pack(
                            user_id = userId,
                            name = packName
                        )
                    ).toInt()
                }

                withContext(Dispatchers.IO) {
                    cardInPackAdapter.getCards().forEach { card ->
                        db.packCardDao().insert(
                            PackCard(
                                pack_id = packId,
                                card_id = card.id,
                                lastReview = Date()
                            )
                        )
                    }
                }

                Log.d("PACK", "Pack saved id=$packId author=$userId")
                finish()
            }
        }

    }

    private fun getCurrentUserId(): Int {
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        return prefs.getInt("user_id", -1)
    }

    private val chooseCardsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedCards =
                    result.data?.getParcelableArrayListExtra<Card>("selected_cards")
                if (!selectedCards.isNullOrEmpty()) {
                    cardInPackAdapter.addCards(selectedCards)
                }
            }
        }

}