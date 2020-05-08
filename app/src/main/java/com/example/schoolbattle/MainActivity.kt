package com.example.schoolbattle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_game_item.view.*
import kotlinx.android.synthetic.main.activity_game_list.*

lateinit var gamesRecycler: RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        val prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val globalName = prefs.getString("username", "")
        toolbarName.text = globalName

        setupRecyclerView(item_list)
        gamesRecycler = item_list

        myRef.child("Users").child(globalName.toString()).child("Games").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                GAMES.add(Game(p0.key.toString()))
                gamesRecycler.adapter?.notifyDataSetChanged()
            }
            override fun onChildRemoved(p0: DataSnapshot) {}
        })

        logOut.setOnClickListener() {
            val prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString("username", "")
            editor.apply()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        newGameButton.setOnClickListener() {
            myRef.child("Users").child(globalName.toString()).child("Games").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.childrenCount == GAMES.size.toLong()) {
                        val intent = Intent(applicationContext, NewGameActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(GAMES)
    }

    class SimpleItemRecyclerViewAdapter(val ITEMS: MutableList<Game>):
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val intent = Intent(v.context, StupidGameActivityTwoPlayers::class.java).apply {
                    //putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id)
                }
                v.context.startActivity(intent)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_game_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.idView.text = ITEMS[position].type + ": You vs"
            holder.contentView.text = ITEMS[position].name
            with(holder.itemView) {
                tag = ITEMS[position]
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount(): Int {
            return ITEMS.size
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
        }
    }
}
