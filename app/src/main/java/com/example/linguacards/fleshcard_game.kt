package com.example.linguacards

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Card
import kotlinx.coroutines.launch
import android.view.ViewConfiguration
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.math.abs

class fleshcard_game : AppCompatActivity() {
    private lateinit var cardContainer: FrameLayout
    private lateinit var knowingCount: TextView
    private lateinit var notKnowingCount: TextView
    private lateinit var bReturnCard: ImageButton
    private lateinit var bFinish: ImageButton
    private lateinit var bPlaySound: ImageButton
    private lateinit var topToolbar: MaterialToolbar

    private var totalCards = 0
    private var currentIndex = 0
    private var tts: android.speech.tts.TextToSpeech? = null


    private val cards = mutableListOf<Card>()
    private val history = mutableListOf<Triple<Card, View, SwipeDirection>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fleshcard_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tts = android.speech.tts.TextToSpeech(this) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                tts?.language = java.util.Locale.ENGLISH // можно сменить на любой язык
            } else {
                Toast.makeText(this, "TTS initialization failed", Toast.LENGTH_SHORT).show()
            }
        }


        cardContainer = findViewById(R.id.cardContainer)
        knowingCount = findViewById(R.id.knowingCount)
        notKnowingCount = findViewById(R.id.notKnowingCount)
        bReturnCard = findViewById(R.id.bReturnCard)
        bFinish = findViewById(R.id.bFinish)
        bPlaySound = findViewById(R.id.bPlaySound)
        topToolbar = findViewById(R.id.topToolbar)

        val db = AppDataBase.getDatabase(this)
        val cardDao = db.cardDao()

        val cardsFromIntent = intent.getParcelableArrayListExtra<Card>("CARDS")
        if (cardsFromIntent != null) {
            cards.clear()
            cards.addAll(cardsFromIntent)
            totalCards = cards.size
            currentIndex = 1
            showCards()
            updateToolbar()
        } else {
            val db = AppDataBase.getDatabase(this)
            lifecycleScope.launch {
                val dbCards = db.cardDao().getAll()
                cards.clear()
                cards.addAll(dbCards)
                totalCards = cards.size
                currentIndex = 1
                showCards()
                updateToolbar()
            }
        }

        bReturnCard.setOnClickListener {
            if (history.isNotEmpty()) {
                val (card, cardView, lastDirection) = history.removeAt(history.lastIndex)

                if (lastDirection == SwipeDirection.RIGHT) decrementKnow()
                else decrementDontKnow()

                cardView.translationX = 0f
                cardView.translationY = 0f
                cardView.rotation = 0f
                cardView.alpha = 1f

                val front = cardView.findViewById<View>(R.id.frontSide)
                val back = cardView.findViewById<View>(R.id.backSide)
                front.visibility = View.VISIBLE
                back.visibility = View.GONE

                (cardView.parent as? FrameLayout)?.removeView(cardView)

                cardContainer.addView(cardView)

                currentIndex--
                if (currentIndex < 1) currentIndex = 1
                updateToolbar()
            } else {
                Toast.makeText(this, "No return cards available", Toast.LENGTH_SHORT).show()
            }
        }

        bFinish.setOnClickListener {
            showExitDialog()
        }

        bPlaySound.setOnClickListener {
            val currentCard = cards.getOrNull(currentIndex - 1)
            if (currentCard != null) {
                tts?.speak(currentCard.term, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Toast.makeText(this, "No card selected", Toast.LENGTH_SHORT).show()
            }
        }


        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitDialog()
                }
            }
        )
    }

    private fun showCards() {
        cardContainer.removeAllViews()
        history.clear()

        for (card in cards) {
            addCardToContainer(card)
        }
    }

    private fun addCardToContainer(card: Card) {
        val cardView = layoutInflater.inflate(R.layout.item_fleshcard, cardContainer, false)

        val frontSide = cardView.findViewById<View>(R.id.frontSide)
        val backSide = cardView.findViewById<View>(R.id.backSide)
        val tvTerm = cardView.findViewById<TextView>(R.id.tvTerm)
        val tvDefinition = cardView.findViewById<TextView>(R.id.tvDefinition)

        tvTerm.text = card.term
        tvDefinition.text = card.definition

        frontSide.visibility = View.VISIBLE
        backSide.visibility = View.GONE

        cardView.setOnClickListener { flipCard(cardView) }

        cardView.setOnTouchListener(
            CardTouchListener(
                cardView,
                card,
                onSwiped = { direction ->
                    history.add(Triple(card, cardView, direction))

                    if (direction == SwipeDirection.RIGHT) incrementKnow()
                    else incrementDontKnow()

                    cardContainer.removeView(cardView)

                    if (currentIndex < totalCards) {
                        currentIndex++
                    }
                    updateToolbar()

                    checkIfEmpty()
                },
                onNotSwiped = { }
            )
        )

        cardContainer.addView(cardView, 0)
    }

    private fun incrementKnow() {
        knowingCount.text = (knowingCount.text.toString().toInt() + 1).toString()
    }

    private fun decrementKnow() {
        knowingCount.text = (knowingCount.text.toString().toInt() - 1).coerceAtLeast(0).toString()
    }

    private fun incrementDontKnow() {
        notKnowingCount.text = (notKnowingCount.text.toString().toInt() + 1).toString()
    }

    private fun decrementDontKnow() {
        notKnowingCount.text = (notKnowingCount.text.toString().toInt() - 1).coerceAtLeast(0).toString()
    }

    private fun updateToolbar() {
        val safeIndex = currentIndex.coerceAtMost(totalCards)
        topToolbar.title = "$safeIndex / $totalCards"
    }

    private fun flipCard(cardView: View) {
        val front = cardView.findViewById<View>(R.id.frontSide)
        val back = cardView.findViewById<View>(R.id.backSide)

        val showingFront = back.visibility == View.GONE

        cardView.animate()
            .rotationY(90f)
            .setDuration(150)
            .withEndAction {
                if (showingFront) {
                    front.visibility = View.GONE
                    back.visibility = View.VISIBLE
                } else {
                    back.visibility = View.GONE
                    front.visibility = View.VISIBLE
                }

                cardView.rotationY = -90f

                cardView.animate()
                    .rotationY(0f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun checkIfEmpty() {
        if (cardContainer.childCount == 0) {
            goToResultScreen()
        }
    }

    enum class SwipeDirection { LEFT, RIGHT }

    inner class CardTouchListener(
        private val cardView: View,
        private val card: Card,
        private val onSwiped: (direction: SwipeDirection) -> Unit,
        private val onNotSwiped: () -> Unit
    ) : View.OnTouchListener {

        private var downX = 0f
        private var downY = 0f
        private var isDragging = false
        private var touchSlop = ViewConfiguration.get(cardView.context).scaledTouchSlop

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {

                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downY = event.rawY
                    isDragging = false
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - downX
                    val dy = event.rawY - downY

                    if (!isDragging && abs(dx) > 20) {
                        isDragging = true
                    }

                    if (isDragging) {
                        cardView.translationX = dx
                        cardView.translationY = dy
                        cardView.rotation = dx / 20f
                    }
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)

                    val dx = event.rawX - downX

                    if (!isDragging) {
                        v.performClick()
                        return true
                    }

                    if (dx > cardView.width * 0.25f) {
                        cardView.animate()
                            .translationX(cardView.width.toFloat())
                            .alpha(0f)
                            .setDuration(200)
                            .withEndAction { onSwiped(SwipeDirection.RIGHT) }
                            .start()
                        return true
                    }

                    if (dx < -cardView.width * 0.25f) {
                        cardView.animate()
                            .translationX(-cardView.width.toFloat())
                            .alpha(0f)
                            .setDuration(200)
                            .withEndAction { onSwiped(SwipeDirection.LEFT) }
                            .start()
                        return true
                    }

                    cardView.animate()
                        .translationX(0f)
                        .translationY(0f)
                        .rotation(0f)
                        .setDuration(200)
                        .withEndAction { onNotSwiped() }
                        .start()

                    return true
                }
            }
            return false
        }
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Finish training?")
            .setMessage("The results will be saved. Are you sure?")
            .setPositiveButton("Yes") { _, _ ->
                goToResultScreen()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun goToResultScreen() {
        val intent = Intent(this, result::class.java)

        intent.putExtra("TOTAL_CARDS", cards.size)
        intent.putExtra("CARDS_KNOWN", knowingCount.text.toString().toInt())
        intent.putExtra("CARDS_DONT_KNOWN", notKnowingCount.text.toString().toInt())

        intent.putParcelableArrayListExtra("CARDS", ArrayList(cards))

        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

}