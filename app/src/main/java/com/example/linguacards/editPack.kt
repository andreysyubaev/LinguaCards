package com.example.linguacards

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.linguacards.adapters.CardInPackAdapter
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Card
import com.example.linguacards.data.model.PackCard
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class editPack : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var rvCardsInPack: RecyclerView
    private lateinit var cardInPackAdapter: CardInPackAdapter
    private lateinit var tvDifficult: TextView
    private lateinit var tvCreator: TextView
    private lateinit var tvCreatedAt: TextView
    private lateinit var tvLastReview: TextView
    private lateinit var bEditPack: Button
    private lateinit var fabAddPack: FloatingActionButton

    private var packId: Int = 0
    private lateinit var db: AppDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_pack)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDataBase.getDatabase(this)

        etTitle = findViewById(R.id.etTitle)
        rvCardsInPack = findViewById(R.id.rvCards)
        tvDifficult = findViewById(R.id.tvDifficult)
        tvCreator = findViewById(R.id.tvCreator)
        tvCreatedAt = findViewById(R.id.tvCreatedAt)
        tvLastReview = findViewById(R.id.tvLastReview)
        bEditPack = findViewById(R.id.bEditPack)
        fabAddPack = findViewById(R.id.fabAddPack)

        val toolbar = findViewById<MaterialToolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        packId = intent.getIntExtra("PACK_ID", 0)

        cardInPackAdapter = CardInPackAdapter(mutableListOf()) { updateStats() }
        rvCardsInPack.layoutManager = LinearLayoutManager(this)
        rvCardsInPack.adapter = cardInPackAdapter

        loadPack()

        fabAddPack.setOnClickListener {
            val intent = Intent(this, chooseCardForAddInPack::class.java)
            intent.putParcelableArrayListExtra(
                "cards_in_pack",
                ArrayList(cardInPackAdapter.getCards())
            )
            chooseCardsLauncher.launch(intent)
        }

        bEditPack.setOnClickListener {
            lifecycleScope.launch {
                val newName = etTitle.text.toString().ifBlank { getString(R.string.new_pack) }
                withContext(Dispatchers.IO) {
                    db.packDao().updatePack(packId, newName)

                    db.packCardDao().deleteByPackId(packId)
                    val now = Date()
                    cardInPackAdapter.getCards().forEach { card ->
                        db.packCardDao().insert(
                            PackCard(
                                pack_id = packId,
                                card_id = card.id,
                                createdAt = now,
                                lastReview = now
                            )
                        )
                    }
                }
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun loadPack() {
        lifecycleScope.launch {
            val pack = withContext(Dispatchers.IO) { db.packDao().getById(packId) }
            if (pack == null) return@launch

            etTitle.setText(pack.name)

            val user = withContext(Dispatchers.IO) {
                db.userDao().getById(pack.user_id)
            }
            tvCreator.text = " ${user?.username ?: "Unknown"}"

            tvCreatedAt.text =
                " ${
                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        .format(pack.createdAt)
                }"

            val packCards = withContext(Dispatchers.IO) {
                db.packCardDao().getByPackId(packId)
            }

            val cards = withContext(Dispatchers.IO) {
                packCards.mapNotNull { pc ->
                    db.cardDao().getCardsByIds(listOf(pc.card_id)).firstOrNull()
                }
            }

            cardInPackAdapter.addCards(cards)
            updateStats()
        }
    }


    private fun updateStats() {
        lifecycleScope.launch {
            val packCards = withContext(Dispatchers.IO) { db.packCardDao().getByPackId(packId) }

            if (packCards.isEmpty()) {
                tvDifficult.text = " 0.0 ★"
                tvLastReview.text = " N/A"
                return@launch
            }

            // Получаем карты по id
            val cards = withContext(Dispatchers.IO) {
                packCards.mapNotNull { pc -> db.cardDao().getCardsByIds(listOf(pc.card_id)).firstOrNull() }
            }

            // Средняя сложность
            val avgEase = if (cards.isNotEmpty()) cards.map { it.easeFactor }.average() else 0.0

            // Последний обзор
            val lastReviewTime = packCards.mapNotNull { it.lastReview }.maxOfOrNull { it.time }

            tvDifficult.text = " %.2f ★".format(avgEase)
            tvLastReview.text = " ${lastReviewTime?.let { Date(it).toLocaleString() } ?: "N/A"}"
        }
    }


    private val chooseCardsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedCards =
                    result.data?.getParcelableArrayListExtra<Card>("selected_cards")
                if (!selectedCards.isNullOrEmpty()) {
                    cardInPackAdapter.addCards(selectedCards)
                    updateStats()
                }
            }
        }
}