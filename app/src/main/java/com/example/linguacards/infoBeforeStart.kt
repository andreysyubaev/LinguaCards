package com.example.linguacards

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.adapters.CardInPackAdapter
import com.example.linguacards.adapters.CardInPackInfoAdapter
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Card
import com.example.linguacards.data.model.Pack
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class infoBeforeStart : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvCardsCount: TextView
    private lateinit var tvCreator: TextView
    private lateinit var tvDifficult: TextView

    private lateinit var rvCards: RecyclerView

    private lateinit var bFavorite: Button
    private lateinit var bStart: Button

    private lateinit var cardInPackInfoAdapter: CardInPackInfoAdapter

    private var cards: List<Card> = emptyList()
    private var pack: Pack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info_before_start)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainScreen::class.java)
            startActivity(intent)
            finish()
        }

        tvName = findViewById(R.id.tvName)
        tvCardsCount = findViewById(R.id.tvCardsCount)
        tvCreator = findViewById(R.id.tvCreator)
        tvDifficult = findViewById(R.id.tvDifficult)

        rvCards = findViewById(R.id.rvCards)

        bFavorite = findViewById(R.id.bFavorite)
        bStart = findViewById(R.id.bStart)

        bFavorite.setOnClickListener {
            pack?.let { p ->
                lifecycleScope.launch {
                    val db = AppDataBase.getDatabase(this@infoBeforeStart)
                    val newState = !p.isFavorite
                    db.packDao().setFavorite(p.id, newState)
                    pack = p.copy(isFavorite = newState)

                    bFavorite.text = if (newState) "Remove from Favorites" else "Add to Favorites"
                }
            }
        }

        val packId = intent.getIntExtra("pack_id", -1)
        if (packId == -1) {
            finish()
            return
        }

        cardInPackInfoAdapter = CardInPackInfoAdapter(mutableListOf())

        rvCards.layoutManager = LinearLayoutManager(this)
        rvCards.adapter = cardInPackInfoAdapter

        lifecycleScope.launch {
            val db = AppDataBase.getDatabase(this@infoBeforeStart)

            val p = db.packDao().getById(packId) ?: return@launch
            pack = p

            val cardIds = db.packCardDao().getCardIds(packId)
            cards = if (cardIds.isNotEmpty()) db.cardDao().getCardsByIds(cardIds) else emptyList()

            tvName.text = p.name
            tvCardsCount.text = cards.size.toString()
            tvDifficult.text =
                if (cards.isNotEmpty())
                    "%.2f ★".format(cards.map { it.easeFactor }.average())
                else "0 ★"

            val user = db.userDao().getById(p.user_id)
            tvCreator.text = user?.username ?: "Unknown"

            cardInPackInfoAdapter.setCards(cards)

            bFavorite.text = if (p.isFavorite) "Remove pack from Favorites" else "Add pack to Favorites ★"
        }

        bStart.setOnClickListener {
            val intent = Intent(this, fleshcard_game::class.java)
            intent.putParcelableArrayListExtra("CARDS", ArrayList(cards))
            startActivity(intent)
        }
    }
}