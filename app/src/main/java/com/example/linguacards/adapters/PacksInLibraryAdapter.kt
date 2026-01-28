package com.example.linguacards.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.R
import com.example.linguacards.dao.CardDao
import com.example.linguacards.dao.PackCardDao
import com.example.linguacards.data.model.Pack
import com.example.linguacards.fleshcard_game
import com.example.linguacards.infoBeforeStart
import kotlinx.coroutines.launch

class PackLibraryAdapter(
    private var packs: List<Pack>,
    private val packCardDao: PackCardDao,
    private val cardDao: CardDao,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onPlayClick: (Pack) -> Unit
) : RecyclerView.Adapter<PackLibraryAdapter.PackViewHolder>() {

    inner class PackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvDifficult: TextView = itemView.findViewById(R.id.tvDifficult)
        val tvCardsCount: TextView = itemView.findViewById(R.id.tvCardsCount)
        val bPlay: ImageButton = itemView.findViewById(R.id.bPlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pack_in_library, parent, false)
        return PackViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackViewHolder, position: Int) {
        val pack = packs[position]
        holder.tvName.text = pack.name
        holder.itemView.isSelected = pack.isFavorite

        // Динамическая сложность и количество карточек
        lifecycleScope.launch {
            val count = packCardDao.getCardsCount(pack.id)
            val cardIds = packCardDao.getCardIds(pack.id)
            val cards = cardDao.getCardsByIds(cardIds)
            val avgEase = if (cards.isNotEmpty()) cards.map { it.easeFactor }.average() else 0.0

            holder.tvCardsCount.text = " $count"
            holder.tvDifficult.text = "%.2f ★".format(avgEase)
        }

        holder.bPlay.setOnClickListener {
//            lifecycleScope.launch {
//                // получаем все карточки этого пака
//                val packCards = packCardDao.getByPackId(pack.id)
//                val cardIds = packCards.map { it.card_id }
//                val cards = cardDao.getCardsByIds(cardIds)  // <-- используем cardDao, который передан в адаптер
//
//                // запускаем тренировку
//                val intent = Intent(holder.itemView.context, fleshcard_game::class.java)
//                intent.putParcelableArrayListExtra("CARDS", ArrayList(cards))
//                holder.itemView.context.startActivity(intent)
//            }

            val intent = Intent(holder.itemView.context, infoBeforeStart::class.java)
            intent.putExtra("pack_id", pack.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = packs.size

    fun updateList(newPacks: List<Pack>) {
        packs = newPacks
        notifyDataSetChanged()
    }
}
