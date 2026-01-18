package com.example.linguacards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.R
import com.example.linguacards.data.model.Card

class CardInPackInfoAdapter(
    private var cards: MutableList<Card>
) : RecyclerView.Adapter<CardInPackInfoAdapter.CardInPackInfoViewHolder>() {

    inner class CardInPackInfoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvTerm: TextView = itemView.findViewById(R.id.tvTerm)
        val tvDefinition: TextView = itemView.findViewById(R.id.tvDefinition)
        val tvDifficult: TextView = itemView.findViewById(R.id.tvDifficult)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CardInPackInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_in_pack_info, parent, false)
        return CardInPackInfoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CardInPackInfoViewHolder,
        position: Int
    ) {
        val card = cards[position]
        holder.tvTerm.text = card.term
        holder.tvDefinition.text = card.definition
        holder.tvDifficult.text = "${card.easeFactor} ★"
    }

    override fun getItemCount() = cards.size

    fun setCards(newCards: List<Card>) {
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }
}
