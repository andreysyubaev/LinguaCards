package com.example.linguacards

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.adapters.CardAdapter
import com.example.linguacards.adapters.ChooseCardAdapter
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Card
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class chooseCardForAddInPack : AppCompatActivity() {
    private lateinit var rvCards: RecyclerView
    private lateinit var bAddCard: Button
    private lateinit var adapter: ChooseCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_card_for_add_in_pack)
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

        rvCards = findViewById(R.id.rvCards)
        bAddCard = findViewById(R.id.bAddCard)

        val alreadyInPack = intent.getParcelableArrayListExtra<Card>("cards_in_pack") ?: emptyList()
        adapter = ChooseCardAdapter(emptyList(), { _, _ -> }, alreadyInPack)

        rvCards.layoutManager = LinearLayoutManager(this)
        rvCards.adapter = adapter

        loadCardsFromDb()

        bAddCard.setOnClickListener {
            val selectedCards = adapter.getSelectedCards()
            if (selectedCards.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putParcelableArrayListExtra(
                    "selected_cards",
                    ArrayList(selectedCards)
                )
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

    }

    private fun loadCardsFromDb() {
        val db = AppDataBase.getDatabase(this)
        val cardDao = db.cardDao()
        lifecycleScope.launch {
            val cards = cardDao.getAll()
            adapter.updateList(cards)
        }
    }
}
