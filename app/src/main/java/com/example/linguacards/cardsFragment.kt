package com.example.linguacards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.adapters.CardAdapter
import com.example.linguacards.data.model.AppDataBase
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
    private lateinit var adapter: CardAdapter
    private lateinit var bEdit: ImageButton
    private lateinit var bTrash: ImageButton
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cards, container, false)
        rvCards = view.findViewById(R.id.rvCards)


        adapter = CardAdapter(emptyList()) { cardToDelete ->
            val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Удаление карточки")
                .setMessage("Вы точно хотите удалить эту карточку?")
                .setPositiveButton("Да") { _, _ ->
                    val db = AppDataBase.getDatabase(requireContext())
                    lifecycleScope.launch {
                        db.cardDao().delete(cardToDelete)
                        val updatedList = db.cardDao().getAll()
                        adapter.updateList(updatedList)
                    }
                }
                .setNegativeButton("Нет") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()

            dialog.show()
        }
        rvCards.layoutManager = LinearLayoutManager(requireContext())
        rvCards.adapter = adapter

        bEdit = view.findViewById(R.id.bEdit)
        bTrash = view.findViewById(R.id.bTrash)

        loadCardsFromDb()

        return view
    }

    private fun loadCardsFromDb() {
        val db = AppDataBase.getDatabase(requireContext())
        val cardDao = db.cardDao()

        lifecycleScope.launch {
            val cards = cardDao.getAll()
            adapter.updateList(cards)
        }
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