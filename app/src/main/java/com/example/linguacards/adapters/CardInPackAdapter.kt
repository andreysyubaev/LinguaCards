package com.example.linguacards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.R
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Card
import kotlinx.coroutines.launch

class CardInPackAdapter(
    private var cards: MutableList<Card>,
    private val onCardsChanged: ((cards: List<Card>) -> Unit)? = null
) : RecyclerView.Adapter<CardInPackAdapter.CardInPackViewHolder>() {

    inner class CardInPackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTerm: TextView = itemView.findViewById(R.id.tvTerm)
        val tvDefinition: TextView = itemView.findViewById(R.id.tvDefinition)
        val tvDifficult: TextView = itemView.findViewById(R.id.tvDifficult)
        val bTrash: ImageButton = itemView.findViewById(R.id.bTrash)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardInPackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_in_creating_pack, parent, false)
        return CardInPackViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardInPackViewHolder, position: Int) {
        val card = cards[position]
        holder.tvTerm.text = card.term
        holder.tvDefinition.text = card.definition
        holder.tvDifficult.text = "${card.easeFactor} ★"

        holder.bTrash.setOnClickListener {
            val context = holder.itemView.context
            androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Удаление карточки")
                .setMessage("Вы точно хотите удалить эту карточку?")
                .setPositiveButton("Да") { _, _ ->
                    removeCardAt(holder.adapterPosition)
                }
                .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    override fun getItemCount(): Int = cards.size

    fun addCards(newCards: List<Card>) {
        val existingIds = cards.map { it.id }.toSet()

        val uniqueNewCards = newCards.filter { it.id !in existingIds }

        if (uniqueNewCards.isNotEmpty()) {
            cards.addAll(uniqueNewCards)
            notifyDataSetChanged()
            onCardsChanged?.invoke(cards)
        }
    }

    fun removeCardAt(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            cards.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cards.size)
            onCardsChanged?.invoke(cards) // уведомляем об удалении
        }
    }

    fun getCards(): List<Card> = cards
}