package com.example.linguacards

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainScreen : AppCompatActivity() {
    private val SYSTEM_LOGIN = "system"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        val navMenuBtn = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val controller = findNavController(R.id.fragmentContainerView)
//        val fabAddCard: FloatingActionButton = findViewById(R.id.fabAddCard)
//        val fabAddPack: FloatingActionButton = findViewById(R.id.fabAddPack)
        navMenuBtn.setupWithNavController(controller)

//        controller.addOnDestinationChangedListener { _, destination, arguments ->
//            when (destination.id) {
//                R.id.cardsFragment -> fabAddCard.show()
//                else -> fabAddCard.hide()
//            }
//        }
//
//        controller.addOnDestinationChangedListener { _, destination, arguments ->
//            when (destination.id) {
//                R.id.packsFragment -> fabAddPack.show()
//                else -> fabAddPack.hide()
//            }
//        }

//        fabAddCard.setOnClickListener {
//            val intent = Intent(this, addCard::class.java)
//            startActivity(intent)
//        }
//
//        fabAddPack.setOnClickListener {
//            val intent = Intent(this, addpack::class.java)
//            startActivity(intent)
//        }
    }
}