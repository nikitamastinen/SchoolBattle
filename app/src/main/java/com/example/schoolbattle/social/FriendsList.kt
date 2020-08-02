package com.example.schoolbattle.social

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolbattle.*
import com.example.schoolbattle.shop.HELPED_CONTEXT
import com.example.schoolbattle.shop.locale_context
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_designs.*
import kotlinx.android.synthetic.main.activity_friends_and_followers.*
import kotlinx.android.synthetic.main.activity_friends_and_followers_item.view.*
import kotlinx.android.synthetic.main.activity_friends_item.view.*
import kotlinx.android.synthetic.main.activity_friends_list.*


class FriendsList : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_friends_and_followers, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        locale_context = activity as AppCompatActivity
        if (Design == "Egypt"){
            back_friends_followers.setBackgroundResource(R.drawable.background_egypt)
            //nav_view.setBackgroundColor(rgb(224, 164, 103));
            //my_toolbar2.setBackgroundColor(rgb(224,164,103))
            //id_text.setTypeface(ResourcesCompat.getFont(CONTEXT, R.font.s))
            //content.setTypeface(ResourcesCompat.getFont(CONTEXT, R.font.s))
            //id_text.setTextColor(Color.WHITE)
        }
        else if (Design == "Casino"){
            back_friends_followers.setBackgroundResource(R.drawable.background2_casino)
        }
        else if (Design == "Rome"){
            back_friends_followers.setBackgroundResource(R.drawable.background_rome)
        }
        val prefs = locale_context!!.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        var username = prefs?.getString("username", "")
        if (locale_context!!.intent.getStringExtra("curName") != null) {
            username = locale_context!!.intent.getStringExtra("curName")
        }
        Toast.makeText(locale_context, username, Toast.LENGTH_LONG).show()
        val friendsIn = mutableListOf<String>()

        recyclerViewFriendsAndFollowers.adapter =
            ItemRecyclerViewAdapter(friendsIn)
        myRef.child("Users").child(username.toString()).child("FriendsIn").addChildEventListener(object:
            ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                friendsIn.add(p0.value as String)
                if(recyclerViewFriendsAndFollowers?.adapter!=null)
                {
                    (recyclerViewFriendsAndFollowers?.adapter as ItemRecyclerViewAdapter).notifyItemInserted(friendsIn.size - 1)
                }

            }

            override fun onChildRemoved(p0: DataSnapshot) {
                friendsIn.remove(p0.value as String)
                if(recyclerViewFriendsAndFollowers!!.adapter != null)
                {
                    recyclerViewFriendsAndFollowers.adapter?.notifyDataSetChanged()
                }

            }
        })

    }
}


class ItemRecyclerViewAdapter(private val ITEMS: MutableList<String>):
    RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val intent = Intent(v.context, ProfileUserActivity::class.java)
            intent.putExtra("name", v.tag as String)
            v.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_friends_and_followers_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.idView.text = ITEMS[position]
        with(holder.itemView) {
            tag = ITEMS[position]
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int {
        return ITEMS.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.textViewFriendsAndFollowers
    }
}


