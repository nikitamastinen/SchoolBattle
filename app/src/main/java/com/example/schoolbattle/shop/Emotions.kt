package com.example.schoolbattle.shop

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Color.rgb
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.schoolbattle.*
import kotlinx.android.synthetic.main.activity_designs.*
import kotlinx.android.synthetic.main.design_shop_item.view.*
import kotlinx.android.synthetic.main.internet_dialog.*
import kotlinx.android.synthetic.main.shop_dialog.*

class Emotions : Fragment()  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_designs, container, false)

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //     vasa = activity.setContentView(R.layout.activity_shop_fragment)

        choose_design_shop.text = "Купленные эмоции вы сможете использовать во время игры"
        HELPED_CONTEXT = activity

        locale_context = activity as AppCompatActivity

        //  vasa = (activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.activity_shop_fragment, null)
        //   (activity as AppCompatActivity?)!!.setSupportActionBar(tb1_shop_design)
        ShopEMOTIONsetupRecyclerView(item_design_shop, requireActivity())
        gamesRecycler = item_design_shop
        gamesRecycler.isNestedScrollingEnabled = false;
        item_design_shop.adapter?.notifyDataSetChanged()


        if(Design == "Egypt")
        {
            choose_design_shop.typeface =  locale_context?.let { ResourcesCompat.getFont(it, R.font.egypt) }
            choose_design_shop.setTextColor(Color.BLACK)
            choose_design_shop.textSize = 20f
        }
        else if(Design == "Casino")
        {
            choose_design_shop.typeface =  locale_context?.let { ResourcesCompat.getFont(it, R.font.casino) }
            choose_design_shop.setTextColor(Color.YELLOW)
            choose_design_shop.textSize = 20f
        }
        else if(Design == "Rome")
        {
            choose_design_shop.typeface =  locale_context?.let { ResourcesCompat.getFont(it, R.font.rome) }
            choose_design_shop.setTextColor(rgb(193, 150, 63))
            choose_design_shop.textSize = 25f
        }
        else if(Design == "Gothic")
        {
            choose_design_shop.typeface =  locale_context?.let { ResourcesCompat.getFont(it, R.font.gothic) }
            choose_design_shop.setTextColor(Color.WHITE)
            choose_design_shop.textSize = 25f
        }
        else if(Design == "Japan")
        {
            choose_design_shop.typeface =  locale_context?.let { ResourcesCompat.getFont(it, R.font.japan) }
            choose_design_shop.setTextColor(Color.BLACK)
            choose_design_shop.textSize = 20f
        }
        else if(Design == "Noir")
        {
            choose_design_shop.typeface =  locale_context?.let { ResourcesCompat.getFont(it, R.font.noir) }
            choose_design_shop.setTextColor(Color.WHITE)
            choose_design_shop.textSize = 20f
        }

    }



}




private fun ShopEMOTIONsetupRecyclerView(recyclerView: RecyclerView, activity: Activity) {
    recyclerView.adapter = ShopEMOTIONsItemRecyclerViewAdapter(ARRAY_OF_EMOTION_SHOP, activity)
}



class ShopEMOTIONsItemRecyclerViewAdapter(private val DESIGN_ITEMS: MutableList<Int>, val activity: Activity):
    RecyclerView.Adapter<ShopEMOTIONsItemRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            var item = v.tag

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.design_shop_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        PICTURE_EMOTION[ARRAY_OF_EMOTION_SHOP[position]]?.let { holder.img.setBackgroundResource(it) }     //картинка для стиля
        holder.price.text = PRICE_OD_EMOTION[ARRAY_OF_EMOTION_SHOP[position]].toString()             //цена стиля
        holder.contentView.text = EMOTION_TEXT[ARRAY_OF_EMOTION_SHOP[position]]          //название стиля

        if(ARRAY_OF_EMOTION_SHOP[position] in  ARRAY_OF_EMOTION)         //если дизайн уже куплен
        {
            holder.button.text = "(КУПЛЕНО)"
            holder.button.background = null
            holder.price.text = ""
        }
        else
        {
            holder.icon.setImageResource(R.drawable.money)
        }
        if(Design == "Egypt"){
            holder.background_item.setBackgroundColor(Color.rgb(255, 230, 163))

            holder.button.textSize = 20f        //так задаешь размер
            holder.button.setTextColor(Color.BLACK)   //цвет
            holder.button.setBackgroundColor(Color.argb(0, 0, 0, 0))
            holder.button.typeface = ResourcesCompat.getFont(locale_context!!, R.font.egypt)

            holder.price.textSize = 20f        //так задаешь размер
            holder.price.setTextColor(Color.BLACK)   //цвет
            holder.price.typeface = ResourcesCompat.getFont(locale_context!!, R.font.egypt)

            holder.contentView.textSize = 20f        //так задаешь размер
            holder.contentView.setTextColor(Color.BLACK)   //цвет
            holder.contentView.typeface = ResourcesCompat.getFont(locale_context!!, R.font.egypt)
        }
        else if (Design == "Casino"){
            holder.background_item.setBackgroundResource(R.drawable.table)
            holder.button.textSize = 20f        //так задаешь размер
            holder.button.setTextColor(Color.YELLOW)   //цвет
            holder.button.setBackgroundColor(Color.argb(0, 0, 0, 0))
            holder.button.typeface = ResourcesCompat.getFont(locale_context!!, R.font.casino)

            holder.price.textSize = 20f        //так задаешь размер
            holder.price.setTextColor(Color.YELLOW)   //цвет
            holder.price.typeface = ResourcesCompat.getFont(locale_context!!, R.font.casino)

            holder.contentView.textSize = 20f        //так задаешь размер
            holder.contentView.setTextColor(Color.YELLOW)   //цвет
            holder.contentView.typeface = ResourcesCompat.getFont(locale_context!!, R.font.casino)
        }
        else if (Design == "Rome"){
            holder.background_item.setBackgroundResource(R.drawable.bottom_navigation_rome)
            holder.button.textSize = 20f        //так задаешь размер
            holder.button.setBackgroundColor(Color.argb(0, 0, 0, 0))
            holder.button.setTextColor(rgb(193, 150, 63))   //цвет
            holder.button.typeface = ResourcesCompat.getFont(locale_context!!, R.font.rome)

            holder.price.textSize = 20f        //так задаешь размер
            holder.price.setTextColor(rgb(193, 150, 63))   //цвет
            holder.price.typeface = ResourcesCompat.getFont(locale_context!!, R.font.rome)

            holder.contentView.textSize = 20f        //так задаешь размер
            holder.contentView.setTextColor(rgb(193, 150, 63))   //цвет
            holder.contentView.typeface = ResourcesCompat.getFont(locale_context!!, R.font.rome)
        }
        else if (Design == "Gothic"){
            holder.background_item.setBackgroundColor(rgb(20,20,20))
            holder.button.textSize = 16f        //так задаешь размер
            holder.button.setBackgroundColor(rgb(30,30,30))
            holder.button.setTextColor(Color.WHITE)   //цвет
            holder.button.typeface = ResourcesCompat.getFont(locale_context!!, R.font.gothic)

            holder.price.textSize = 20f        //так задаешь размер
            holder.price.setTextColor(Color.WHITE)   //цвет
            holder.price.typeface = ResourcesCompat.getFont(locale_context!!, R.font.gothic)

            holder.contentView.textSize = 20f        //так задаешь размер
            holder.contentView.setTextColor(Color.WHITE)   //цвет
            holder.contentView.typeface = ResourcesCompat.getFont(locale_context!!, R.font.gothic)
        }
        else if (Design == "Japan"){
            holder.background_item.setBackgroundColor(Color.WHITE)
            holder.button.textSize = 20f        //так задаешь размер
            holder.button.setTextColor(Color.BLACK)   //цвет
            holder.button.typeface = ResourcesCompat.getFont(locale_context!!, R.font.japan)

            holder.price.textSize = 20f        //так задаешь размер
            holder.price.setTextColor(Color.BLACK)   //цвет
            holder.price.typeface = ResourcesCompat.getFont(locale_context!!, R.font.japan)

            holder.contentView.textSize = 20f        //так задаешь размер
            holder.contentView.setTextColor(Color.BLACK)   //цвет
            holder.contentView.typeface = ResourcesCompat.getFont(locale_context!!, R.font.japan)
        }
        else if (Design == "Noir"){
            holder.background_item.setBackgroundColor(rgb(20,20,20))
            holder.button.textSize = 20f        //так задаешь размер
            holder.button.setBackgroundColor(rgb(30,30,30))
            holder.button.setTextColor(Color.WHITE)   //цвет
            holder.button.typeface = ResourcesCompat.getFont(locale_context!!, R.font.noir)

            holder.price.textSize = 20f        //так задаешь размер
            holder.price.setTextColor(Color.WHITE)   //цвет
            holder.price.typeface = ResourcesCompat.getFont(locale_context!!, R.font.noir)

            holder.contentView.textSize = 20f        //так задаешь размер
            holder.contentView.setTextColor(Color.WHITE)   //цвет
            holder.contentView.typeface = ResourcesCompat.getFont(locale_context!!, R.font.noir)
        }

        with(holder.itemView) {
            tag = ARRAY_OF_EMOTION_SHOP[position]
        }

        holder.button.setOnClickListener{
            var dialog_internet = Dialog(locale_context!!)
            dialog_internet.setContentView(R.layout.internet_dialog)
            fun buy_emotions(): Boolean
            {
                //TODO MONEY = MONEY from firebase
                //TODO ARRAY_OF_EMOTION = ARRAY_OF_EMOTION from firebase
                if(ARRAY_OF_EMOTION_SHOP[position] !in ARRAY_OF_EMOTION)          //если дизайн не куплен
                {
                    // dialog = Proof_of_purchase(HELPED_CONTEXT!!,locale_context!!,"Design",ARRAY_OF_DESIGN_SHOP[position].toString().toInt(), PRICE_OD_DESIGN[ARRAY_OF_DESIGN_SHOP[position].toString().toInt()]!!)
                    //   dialog?.showResult()
                    var dialog_shop = Dialog(HELPED_CONTEXT!!)
                    dialog_shop.setContentView(R.layout.shop_dialog)

                    dialog_shop.price_shop.text = holder.price.text
                    dialog_shop.description.text = "Купить <" + EMOTION_TEXT[ARRAY_OF_EMOTION_SHOP[position]] + "> за"

                    dialog_shop.show()
                    dialog_shop.buy_shop_dialog.setOnClickListener {
                        val username = activity.getSharedPreferences("UserData", Context.MODE_PRIVATE).getString("username", "").toString()
                        MONEY -= holder.price.text.toString().toInt()
                        ARRAY_OF_EMOTION.add(ARRAY_OF_EMOTION_SHOP[position])
                        val pushMap = mapOf(
                            "Users/$username/money" to MONEY,
                            "Users/$username/array_of_emotions" to CODE(ARRAY_OF_EMOTION)
                        )
                        myRef.updateChildren(pushMap).addOnSuccessListener {    
                            Toast.makeText(activity, "failed to do operation", Toast.LENGTH_LONG).show()
                            //TODO MONEY передать в базу ------- сделано в строках 256 - 262
                            //TODO ARRAY_OF_EMOTION передать в базу ------- сделано в строках 256 - 262
                            holder.price.text = ""
                            holder.icon.setImageResource(R.drawable.nulevoe)
                            holder.button.setBackgroundColor(Color.argb(0, 0, 0, 0))
                            holder.button.text = "(КУПЛЕНО)"
                            locale_context!!.findViewById<TextView>(R.id.money_shop_toolbar).text =
                                MONEY.toString()

                            val editor =
                                locale_context!!.getSharedPreferences("UserData", Context.MODE_PRIVATE)
                                    .edit()
                            editor.putString("money", MONEY.toString())
                            editor.putString("open_emotions", CODE(ARRAY_OF_EMOTION))
                            editor.apply()

                            dialog_shop.dismiss()
                        }
                    }
                    dialog_shop.button_close_2_shop_dialog.setOnClickListener {
                        dialog_shop.dismiss()
                    }
                    dialog_shop.button_close_shop_dialog.setOnClickListener {
                        dialog_shop.dismiss()
                    }

                }
                return true
            }
            fun check_internet():Boolean
            {
                if(verifyAvailableNetwork(locale_context!!))
                {
                    buy_emotions()
                }
                else
                {
                    dialog_internet.show()
                    dialog_internet.button_update.setOnClickListener {
                        dialog_internet.dismiss()
                        check_internet()
                    }
                }
                return true
            }

            check_internet()
        }
    }

    override fun getItemCount(): Int {
        //Toast.makeText(currentContext,ARRAY_OF_DESIGN_SHOP.size.toString(), Toast.LENGTH_LONG).show()
        return ARRAY_OF_EMOTION_SHOP.size


    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var img: ImageView = view.img_ava_shop
        var contentView: TextView = view.id_text_design_shop
        var price: TextView = view.price_design
        var icon: ImageView = view.icon_money_in_price_design
        var button: Button = view.item_button_shop_design
        var background_item: CardView = view.card_design_shop
    }
}