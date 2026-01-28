package com.example.linguacards

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linguacards.adapters.PackAdapter
import com.example.linguacards.data.model.AppDataBase
import com.example.linguacards.data.model.Card
import com.example.linguacards.data.model.Pack
import kotlinx.coroutines.launch
import android.widget.Toast

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [packsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class packsFragment : Fragment() {

    private lateinit var rvPacks: RecyclerView
    private lateinit var svPacks: SearchView
    private lateinit var bAddPack: ImageButton
    private lateinit var adapter: PackAdapter
    private lateinit var db: AppDataBase

    private var currentQuery: String = ""
    private var allPacks = listOf<Pack>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_packs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvPacks = view.findViewById(R.id.rvPacks)
        svPacks = view.findViewById(R.id.svPacks)
        bAddPack = view.findViewById(R.id.bAddPack)
        db = AppDataBase.getDatabase(requireContext())

//        adapter = PackAdapter(
//            packs = emptyList(),
//            packCardDao = db.packCardDao(),
//            lifecycleScope = viewLifecycleOwner.lifecycleScope,
//            onDelete = { pack ->
//                viewLifecycleOwner.lifecycleScope.launch {
//                    db.packDao().delete(pack)
//                    loadPacks()
//                }
//            },
//            onEdit = { pack ->
//                val intent = Intent(requireContext(), editPack::class.java)
//                intent.putExtra("PACK_ID", pack.id)
//                intent.putExtra("PACK_USERID", pack.user_id)
//                intent.putExtra("PACK_NAME", pack.name)
//                intent.putExtra("PACK_CREATEDAT", pack.createdAt)
//                startActivity(intent)
//            }
//        )

        adapter = PackAdapter(
            packs = emptyList(),
            packCardDao = db.packCardDao(),
            lifecycleScope = viewLifecycleOwner.lifecycleScope,

            onDelete = { pack ->
                viewLifecycleOwner.lifecycleScope.launch {
                    db.packDao().delete(pack)
                    loadPacks()
                }
            },

            onEdit = { pack ->
                val intent = Intent(requireContext(), editPack::class.java)
                intent.putExtra("PACK_ID", pack.id)
                intent.putExtra("PACK_USERID", pack.user_id)
                intent.putExtra("PACK_NAME", pack.name)
                intent.putExtra("PACK_CREATEDAT", pack.createdAt)
                startActivity(intent)
            },

            onOpenPack = { pack ->
                val intent = Intent(requireContext(), infoBeforeStart::class.java)
                intent.putExtra("pack_id", pack.id)
                startActivity(intent)
            }
        )

        rvPacks.layoutManager = LinearLayoutManager(requireContext())
        rvPacks.adapter = adapter

        svPacks.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

        svPacks.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                svPacks.isIconified = true
            } else {
                svPacks.isIconified = false
            }
        }

        bAddPack.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {

                val cardsCount = db.cardDao().getCardsCount()

                if (cardsCount < 2) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.to_create_a_pack_you_need),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val intent = Intent(requireContext(), addpack::class.java)
                startActivity(intent)
            }
        }

        loadPacks()
    }

    override fun onResume() {
        super.onResume()
        loadPacks()
    }

    private fun loadPacks() {
        viewLifecycleOwner.lifecycleScope.launch {
            allPacks = db.packDao().getAll()
            applyFilter()
        }
    }

    private fun applyFilter() {
        val filtered = if (currentQuery.isBlank()) {
            allPacks
        } else {
            allPacks.filter {
                it.name.contains(currentQuery, ignoreCase = true)
            }
        }
        adapter.updateList(filtered)
    }

    private val addPackLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            loadPacks()
        }
    }
}