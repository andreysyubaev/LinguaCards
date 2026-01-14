package com.example.linguacards.adapters

import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.data.model.Card
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.linguacards.R
import java.text.SimpleDateFormat
import java.util.Locale

class CardAdapter(private var cards: List<Card>, private val onDelete: (Card) -> Unit) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTerm: TextView = itemView.findViewById(R.id.tvTerm)
        val tvDefinition: TextView = itemView.findViewById(R.id.tvDefinition)
        val tvDifficult: TextView = itemView.findViewById(R.id.tvDifficult)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
        val bTrash: ImageButton = itemView.findViewById(R.id.bTrash)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        holder.tvTerm.text = card.term
        holder.tvDefinition.text = card.definition
        holder.tvDifficult.text = card.easeFactor.toString() + " ★"
        holder.tvCreatedAt.text = card.createdAt.toString()

        val formatter = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        holder.tvCreatedAt.text = formatter.format(card.createdAt)

        holder.bTrash.setOnClickListener {
            onDelete(card)
        }
    }

    override fun getItemCount(): Int = cards.size

    fun updateList(newCards: List<Card>) {
        cards = newCards
        notifyDataSetChanged()
    }
}
