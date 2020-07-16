package com.example.schoolbattle

import android.app.Activity
import android.content.Context
import com.google.firebase.database.ChildEventListener



//______________________________________________________________________
var SOUND: Boolean = false
var VIBRATION: Boolean = false
//____________________________________________________________________________________________________________________________
var Design: String = "Normal"               //дизайн
val PICTURE_STYLES = mapOf(0 to R.drawable.icon_normal_design, 1 to R.drawable.icon_egypt_design, 2 to R.drawable.icon_casino_design,3 to R.drawable.icon_casino_design)
val PICTURE_TEXT = mapOf(0 to "Деловой стиль", 1 to "Eгипетскй стиль", 2 to "Казино стиль",3 to "Римский стиль")
val PRICE_OD_DESIGN = mapOf(0 to 10,1 to 20,2 to 30,3 to 90)
var ARRAY_OF_DESIGN_SHOP: MutableList<Int>  = mutableListOf(0,1,2,3)             //номера  дизайнов в магазине
var ARRAY_OF_DESIGN: MutableList<Int>  = mutableListOf(0,1)             //номера открытых дизайнов
var AUXILIARY_MAP_OF_DESIGNS = mapOf(0 to "Normal", 1 to "Egypt", 2 to "Casino",3 to "Rome")
//__________________________________________________________________________________________________________________________________

var AVATAR : Int = 0                //номер аватарки


var INITIAL_AMOUNT: Int = 100          //начальная сумма
var MONEY: Int = 100                  //ДЕНЬГИ


var GAMES: MutableList<Game> = mutableListOf()
var FRIENDS: MutableList<String> = mutableListOf()
var CHOOSE_GAMES: MutableList<String> = mutableListOf("StupidGame", "XOGame", "DotGame", "GoGame", "SnakeGame", "BoxGame", "AngleGame","VirusGame","Reversi")
var currentContext: Context? = null
lateinit var listener: ChildEventListener
var recyclerSet: RecyclerSet = RecyclerSet()
var recyclerSetBlitz: RecyclerSetBlitz = RecyclerSetBlitz()
lateinit var CONTEXT: Activity


  //__________________________________________________________________для пихания в память телефона
fun CODE(m : MutableList<Int>): String
{
    var s: String = ""
    for(i in m)
    {
        s+=i.toString()
        s+='a'
    }
    return s
}

fun DECODE(s : String): MutableList<Int>
{
    var m: MutableList<Int> = mutableListOf()
    var k: Int = 0
    for(i in s.indices)
    {
        if(s[i]!='a')
        {
            k *= 10
            k += s[i].toInt() - '0'.toInt()
        }
        else
        {
            m.add(k)
            k = 0
        }
    }
    return(m)
}