package com.example.linguacards.adapters

import android.app.AlertDialog
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
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Pack
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PackAdapter (private var packs: List<Pack>,
                   private var packCardDao: PackCardDao,
                   private var lifecycleScope: LifecycleCoroutineScope,
                   private val onDelete: (Pack) -> Unit,
                   private val onEdit: (Pack) -> Unit
) : RecyclerView.Adapter<PackAdapter.PackViewHolder>() {

    inner class PackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
        val tvCardsCount: TextView = itemView.findViewById(R.id.tvCardsCount)
        val tvDifficult: TextView = itemView.findViewById(R.id.tvDifficult)
        val bEdit: ImageButton = itemView.findViewById(R.id.bEdit)
        val bTrash: ImageButton = itemView.findViewById(R.id.bTrash)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pack, parent, false)
        return PackViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackViewHolder, position: Int) {
        val pack = packs[position]

        holder.tvName.text = pack.name
        holder.tvCreatedAt.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            .format(pack.createdAt)

        val db = AppDataBase.getDatabase(holder.itemView.context)

        // Количество карточек и средняя сложность
        lifecycleScope.launch {
            val count = packCardDao.getCardsCount(pack.id)
            holder.tvCardsCount.text = "Cards: $count"

            val cardIds = packCardDao.getCardIds(pack.id)
            val cards = db.cardDao().getCardsByIds(cardIds)
            val avgEase = if (cards.isNotEmpty()) cards.map { it.easeFactor }.average() else 0.0
            holder.tvDifficult.text = "Difficulty: %.2f ★".format(avgEase)
        }

        // Кнопки
        holder.bTrash.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Удаление набора")
                .setMessage("Удалить набор?")
                .setPositiveButton("Да") { _, _ -> onDelete(pack) }
                .setNegativeButton("Нет", null)
                .show()
        }

        holder.bEdit.setOnClickListener { onEdit(pack) }
    }

    override fun getItemCount(): Int = packs.size

    fun updateList(newPacks: List<Pack>) {
        packs = newPacks
        notifyDataSetChanged()
    }
}
