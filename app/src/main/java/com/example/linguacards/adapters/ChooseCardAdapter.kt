package com.example.linguacards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.R
import com.example.linguacards.data.model.Card
import java.text.SimpleDateFormat
import java.util.Locale

class ChooseCardAdapter(
    private var cards: List<Card>,
    private var onCardCheckedChange: (Card, Boolean) -> Unit,
    private val alreadyInPack: List<Card> = emptyList()
) : RecyclerView.Adapter<ChooseCardAdapter.ChooseCardViewHolder>() {

    // Хранение состояния по id карточки
    private val checkedMap = mutableMapOf<Int, Boolean>()

    init {
        // помечаем уже добавленные карточки
        for (card in alreadyInPack) {
            checkedMap[card.id] = true
        }
    }

    inner class ChooseCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTerm: TextView = itemView.findViewById(R.id.tvTerm)
        val tvDefinition: TextView = itemView.findViewById(R.id.tvDefinition)
        val tvDifficult: TextView = itemView.findViewById(R.id.tvDifficult)
        val cbCardChecked: CheckBox = itemView.findViewById(R.id.cbCardChecked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_choosing_card_for_adding_in_pack, parent, false)
        return ChooseCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseCardViewHolder, position: Int) {
        val cardItem = cards[position]

        holder.tvTerm.text = cardItem.term
        holder.tvDefinition.text = cardItem.definition
        holder.tvDifficult.text = cardItem.easeFactor.toString() + " ★"

        holder.cbCardChecked.setOnCheckedChangeListener(null)
        holder.cbCardChecked.isChecked = checkedMap[cardItem.id] ?: false

        holder.cbCardChecked.setOnCheckedChangeListener { _, isChecked ->
            checkedMap[cardItem.id] = isChecked
            onCardCheckedChange(cardItem, isChecked)
        }
    }

    override fun getItemCount(): Int = cards.size

    fun updateList(newCards: List<Card>) {
        cards = newCards
        notifyDataSetChanged()
    }

    fun getSelectedCards(): List<Card> {
        return cards.filter { checkedMap[it.id] == true }
    }
}