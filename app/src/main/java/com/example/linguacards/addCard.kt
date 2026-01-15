package com.example.linguacards

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Card
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch

class addCard : AppCompatActivity() {

    private lateinit var edTerm: EditText
    private lateinit var edDefinition: EditText
    private lateinit var sEaseFactor: Slider
    private lateinit var bAddCard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        edTerm = findViewById(R.id.edTerm)
        edDefinition = findViewById(R.id.edDefinition)
        sEaseFactor = findViewById(R.id.sEaseFactor)
        bAddCard = findViewById(R.id.bAddCard)

        val toolbar = findViewById<MaterialToolbar>(R.id.topToolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val db = AppDataBase.getDatabase(this)
        val cardDao = db.cardDao()

        bAddCard.setOnClickListener {
            val term = edTerm.text.toString().trim()
            val definition = edDefinition.text.toString().trim()

            val easeFactor = sEaseFactor.value.toFloat()

            if (term.isEmpty() || definition.isEmpty()) {
                Toast.makeText(this, "Fill both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val card = Card(term = term, definition = definition, easeFactor = easeFactor)
                cardDao.insert(card)

                edTerm.text.clear()
                edDefinition.text.clear()

                Toast.makeText(this@addCard, "Card added", Toast.LENGTH_SHORT).show()
            }

        }
    }
}