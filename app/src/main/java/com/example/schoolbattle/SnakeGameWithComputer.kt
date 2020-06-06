package com.example.schoolbattle

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_snake_game_with_computer.*

class SnakeGameWithComputer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake_game_with_computer)



        bottom_navigation_snake_with_computer.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_1 ->{

                }
                R.id.page_2 ->{
                    //dialog_parametrs = Show_parametr_with_computer(this@SnakeGameWithComputer)
                    //dialog_parametrs?.showResult_with_computer(this)
                }
                R.id.page_3 ->{
                    this.finish()
                    val intent = Intent(this, SnakeGameWithComputer::class.java).apply {
                        putExtra("usedToClear", "clear")
                    }
                    startActivity(intent)
                }
                R.id.page_4 ->{

                }

            }
            true
        }

    }
}
