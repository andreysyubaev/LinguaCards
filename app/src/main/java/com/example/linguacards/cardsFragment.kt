package com.example.linguacards

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.adapters.CardAdapter
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Card
import com.google.android.material.search.SearchBar
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [cardsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class cardsFragment : Fragment() {
    private lateinit var rvCards: RecyclerView
    private lateinit var svCards: SearchView
    private lateinit var bAddCard: ImageButton
    private lateinit var adapter: CardAdapter
    private lateinit var bEdit: ImageButton
    private lateinit var bTrash: ImageButton
    private lateinit var db: AppDataBase

    private var currentQuery: String = ""
    private var allCards = listOf<Card>()
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        db = AppDataBase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cards, container, false)

        rvCards = view.findViewById(R.id.rvCards)
        svCards = view.findViewById(R.id.svCards)
        bAddCard = view.findViewById(R.id.bAddCard)

        rvCards.layoutManager = LinearLayoutManager(requireContext())

        adapter = CardAdapter(emptyList(),
            onDelete = { cardToDelete ->
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.deleting_card))
                    .setMessage(getString(R.string.are_you_sure_want_to_delete_this_card))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        lifecycleScope.launch {
                            db.cardDao().delete(cardToDelete)
                            loadCardsFromDb()
                        }
                    }
                    .setNegativeButton(getString(R.string.no)) { dialogInterface, _ -> dialogInterface.dismiss() }
                    .show()
            },
            onEdit = { cardToEdit ->
                val intent = Intent(requireContext(), editcard::class.java)
                intent.putExtra("CARD_ID", cardToEdit.id)
                intent.putExtra("CARD_TERM", cardToEdit.term)
                intent.putExtra("CARD_DEFINITION", cardToEdit.definition)
                intent.putExtra("CARD_EASE", cardToEdit.easeFactor)
                startActivity(intent)
            }
        )
        rvCards.adapter = adapter

        svCards.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query.orEmpty()
                applyFilter()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText.orEmpty()
                applyFilter()
                return true
            }
        })

        svCards.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                svCards.isIconified = true
            } else {
                svCards.isIconified = false
            }
        }

        bAddCard.setOnClickListener {
            val intent = Intent(requireContext(), addCard::class.java)
            startActivity(intent)
        }

        loadCardsFromDb()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadCardsFromDb()
    }

    private fun loadCardsFromDb() {
        lifecycleScope.launch {
            allCards = db.cardDao().getAll()
            applyFilter()
        }
    }

    private fun filterCards(query: String) {
        val filtered = allCards.filter {
            it.term.contains(query, ignoreCase = true)
        }
        adapter.updateList(filtered)
    }

    private fun applyFilter() {
        val filtered = if (currentQuery.isBlank()) {
            allCards
        } else {
            allCards.filter { card ->
                card.term.contains(currentQuery, ignoreCase = true) ||
                        card.definition.contains(currentQuery, ignoreCase = true)
            }
        }
        adapter.updateList(filtered)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            cardsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}