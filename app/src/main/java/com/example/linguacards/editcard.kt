package com.example.linguacards

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.linguacards.data.model.AppDataBase
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch

class editcard : AppCompatActivity() {
    private lateinit var edTerm: EditText
    private lateinit var edDefinition: EditText
    private lateinit var sEaseFactor: Slider
    private var cardId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        edTerm = findViewById(R.id.edTerm)
        edDefinition = findViewById(R.id.edDefinition)
        sEaseFactor = findViewById(R.id.sEaseFactor)
        val bEditCard: Button = findViewById(R.id.bEditCard)

        val toolbar = findViewById<MaterialToolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Получаем данные карточки
        cardId = intent.getIntExtra("CARD_ID", 0)
        edTerm.setText(intent.getStringExtra("CARD_TERM"))
        edDefinition.setText(intent.getStringExtra("CARD_DEFINITION"))
        sEaseFactor.value = intent.getFloatExtra("CARD_EASE", 0f)

        bEditCard.setOnClickListener {
            val term = edTerm.text.toString()
            val definition = edDefinition.text.toString()
            val ease = sEaseFactor.value

            val db = AppDataBase.getDatabase(this)

            lifecycleScope.launch {
                db.cardDao().updateCard(cardId, term, definition, ease)
                finish() // закрываем экран редактирования
            }
        }
    }
}