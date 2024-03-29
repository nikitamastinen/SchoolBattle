package com.sga.schoolbattle.gamesonline

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.sga.schoolbattle.*
import com.sga.schoolbattle.engine.BlitzGameEngine
import com.sga.schoolbattle.engine.LongGameEngine
import com.sga.schoolbattle.engine.StupidGame
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_one_device_games_template.*
import kotlinx.android.synthetic.main.activity_online_games_temlate.*
import kotlinx.android.synthetic.main.activity_x_o_game.bottom_navigation_xog_online
import kotlinx.android.synthetic.main.activity_x_o_game.button_player_1_online_xog
import kotlinx.android.synthetic.main.activity_x_o_game.button_player_2_online_xog
import kotlinx.android.synthetic.main.activity_x_o_game.signature_canvas
import kotlinx.android.synthetic.main.activity_x_o_game.timer2_xog_online
import kotlinx.android.synthetic.main.activity_x_o_game.timer_xog_online
import java.util.*

class XOGameActivity : AppCompatActivity() {

    fun encode(h: MutableList<Triple<Int,Int,Int>>):String
    {
        var answer: String = ""
        for(i in 0 until h.size)
        {
            answer = answer + h[i].first.toString() + 'a' + h[i].second.toString() + 'a' + h[i].third.toString() + 'a'
        }
        return answer
    }
    fun string_to_int(s: String): Int
    {
        var i : Int = 0
        var k: Int = 1
        var answer: Int = 0
        while(i<s.length)
        {
            answer += (s[s.length-i-1].toInt() - '0'.toInt())*k
            k= k*10
            i++
        }
        return answer
    }
    fun decode(s : String) : MutableList<Triple<Int,Int,Int>>
    {
        var answer: MutableList<Triple<Int,Int,Int>> = mutableListOf()
        var i : Int = 0
        var a: Int = 0
        var b: Int = 0
        var c: Int = 0
        var s1: String = ""
        while(i<s.length)
        {
            s1 = ""
            while(i < s.length && s[i]!='a')
            {
                s1+=s[i]
                i++
            }
            a = string_to_int(s1)
            s1 = ""
            i++
            while(i < s.length && s[i]!='a')
            {
                s1+=s[i]
                i++
            }
            b = string_to_int(s1)
            s1 = ""
            i++
            while(i < s.length && s[i]!='a')
            {
                s1+=s[i]
                i++
            }
            c = string_to_int(s1)
            answer.add(Triple(a,b,c))
            i++
        }
        return answer
    }

    private var isRun = false
    private var engine: BlitzGameEngine? = null
    private var engineLong: LongGameEngine? = null

    var yourName = ""
    var opponentsName = ""
    var type = ""
    lateinit var gameData: DatabaseReference


    override fun onResume() {
        super.onResume()
        currentContext = this
        isRun = true
        CONTEXT = this
    }


    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        var helpend_var = 0
        mSound.load(this, R.raw.xlup, 1);
        vibratorService = getSystemService(VIBRATOR_SERVICE) as Vibrator

        setContentView(R.layout.activity_online_games_temlate)
        signature_canvas.visibility = View.VISIBLE
        PICTURE_AVATAR[AVATAR]?.let { your_avatar_in_game.setImageResource(it) }
        PICTURE_AVATAR[AVATAR]?.let { avatar_of_protivnic.setImageResource(it) } //TODO заменить это на значения его аватарки

        bottom_navigation_xog_online.itemIconTintList = generateColorStateList()
        bottom_navigation_xog_online.itemTextColor = generateColorStateList()
        if(LANGUAGE == "English")
        {
            bottom_navigation_xog_online.menu.getItem(0).title = "Rules"
            bottom_navigation_xog_online.menu.getItem(1).title = "Settings"
            bottom_navigation_xog_online.menu.getItem(2).title = "Emotions"
            bottom_navigation_xog_online.menu.getItem(3).title = "Back"
            bottom_navigation_xog_online.menu.getItem(4).title = "Next"
        }

        button_player_1_online_xog.textSize = 20f
        button_player_2_online_xog.textSize = 20f
        //                    Toast.makeText(applicationContext,"${signature_canvas.FIELD[checkList[1]][checkList[2]]}", Toast.LENGTH_LONG).show()

        when (Design) {
            "Normal" -> {

            }
            "Egypt" -> {
                label_online.setBackgroundResource(R.drawable.background_egypt)
                toolbar_xog_online.setBackgroundColor(Color.TRANSPARENT)
                toolbar2_xog_online.setBackgroundColor(Color.TRANSPARENT)
                button_player_1_online_xog.setTextColor(Color.BLACK)
                button_player_2_online_xog.setTextColor(Color.BLACK)
                button_player_1_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.egypt)
                button_player_2_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.egypt)
                bottom_navigation_xog_online.setBackgroundColor(Color.rgb(255, 230, 163))
            }
            "Casino" -> {
                label_online.setBackgroundResource(R.drawable.background2_casino)
                toolbar_xog_online.setBackgroundColor(Color.TRANSPARENT)
                toolbar2_xog_online.setBackgroundColor(Color.TRANSPARENT)
                button_player_1_online_xog.setTextColor(Color.YELLOW)
                button_player_2_online_xog.setTextColor(Color.YELLOW)
                button_player_1_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.casino)
                button_player_2_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.casino)
                bottom_navigation_xog_online.setBackgroundResource(R.drawable.bottom_navigation_casino)
            }
            "Rome" -> {
                label_online.setBackgroundResource(R.drawable.background_rome)
                toolbar_xog_online.setBackgroundColor(Color.TRANSPARENT)
                toolbar2_xog_online.setBackgroundColor(Color.TRANSPARENT)
                button_player_1_online_xog.setTextColor(Color.rgb(224, 164, 103))
                button_player_2_online_xog.setTextColor(Color.rgb(224, 164, 103))
                button_player_1_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.rome)
                button_player_2_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.rome)
                bottom_navigation_xog_online.setBackgroundResource(R.drawable.bottom_navigation_rome)
            }
            "Gothic" -> {
                label_online.setBackgroundResource(R.drawable.background_gothic)
                toolbar_xog_online.setBackgroundColor(Color.TRANSPARENT)
                toolbar2_xog_online.setBackgroundColor(Color.TRANSPARENT)
                button_player_1_online_xog.setTextColor(Color.WHITE)
                button_player_2_online_xog.setTextColor(Color.WHITE)
                button_player_1_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.gothic)
                button_player_2_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.gothic)
                bottom_navigation_xog_online.setBackgroundColor(Color.BLACK)
                button_player_1_online_xog.textSize = 16.5f
                button_player_2_online_xog.textSize = 16.5f
            }
            "Japan" -> {
                label_online.setBackgroundResource(R.drawable.background_japan)
                toolbar_xog_online.setBackgroundColor(Color.TRANSPARENT)
                toolbar2_xog_online.setBackgroundColor(Color.TRANSPARENT)
                button_player_1_online_xog.setTextColor(Color.BLACK)
                button_player_2_online_xog.setTextColor(Color.BLACK)
                button_player_1_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.japan)
                button_player_2_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.japan)
                bottom_navigation_xog_online.setBackgroundColor(Color.WHITE)
            }
            "Noir" -> {
                label_online.setBackgroundResource(R.drawable.background_noir)
                toolbar_xog_online.setBackgroundColor(Color.TRANSPARENT)
                toolbar2_xog_online.setBackgroundColor(Color.TRANSPARENT)
                button_player_1_online_xog.setTextColor(Color.WHITE)
                button_player_2_online_xog.setTextColor(Color.WHITE)
                button_player_1_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.noir)
                button_player_2_online_xog.typeface = ResourcesCompat.getFont(CONTEXT, R.font.noir)
            }
            //Emotions начало--------------------------------------------------------------------------------------------
            //Emotion конец-----------------------------------------------------------------------------------------------


            // gameData.child("FIELDD").child("result").onDisconnect().setValue(yourName)
            // gameData.onDisconnect().removeValue()
        }
        currentContext = this
        CONTEXT = this
        isRun = true

        if (StupidGame != Activity()) StupidGame.finish()
        if (NewGame != Activity()) NewGame.finish()
        yourName =
            getSharedPreferences("UserData", Context.MODE_PRIVATE).getString("username", "")
                .toString()

        var type = intent.getStringExtra("type")
        if (type == null) type = ""
        val opponentsName_: String = intent?.getStringExtra("opponent").toString()
        for (i in opponentsName_) {
            if (i == ' ') break
            opponentsName += i
        }
        val yu = if (opponentsName < yourName) '1' else '0'
        val op = if (opponentsName < yourName) '0' else '1'
        gameData = if (intent.getStringExtra("key") != null) {
            myRef.child(type).child("XOGame").child(
                (if (opponentsName < yourName)
                    opponentsName + '_' + yourName + intent.getStringExtra("key")!!  else yourName + '_' + opponentsName + intent.getStringExtra("key")!!)
            )
        } else {
            myRef.child(type).child("XOGame").child(
                (if (opponentsName < yourName)
                    opponentsName + '_' + yourName  else yourName + '_' + opponentsName)
            )
        }
        //Emotions начало--------------------------------------------------------------------------------------------
        initMenuFunctions(this, bottom_navigation_xog_online, intent, yourName, opponentsName, gameData)
        //Emotion конец-----------------------------------------------------------------------------------------------


       // gameData.child("FIELDD").child("result").onDisconnect().setValue(yourName)
       // gameData.onDisconnect().removeValue()
        signature_canvas.blocked = true
        signature_canvas.positionData = gameData
        button_player_1_online_xog.text = yourName
        button_player_2_online_xog.text = opponentsName


        if (type == "blitz") {
            engine = object : BlitzGameEngine {
                override var timer = Timer(true)
                override var cntUser = 0
                override var cntOpponent = 0
                override val userT = timer2_xog_online
                override val opponentT = timer_xog_online
                override val user = yourName
                override val opponent = opponentsName
                override var move = intent.getStringExtra("move") == "1"
                override var positionData = gameData
                override var activity: Activity = this@XOGameActivity
                override var cnt = 0
                override var type = "XOGame"
                override var isFinished = false
                override var userRating = RATING
                override var opponentRating = intent.getStringExtra("rating")!!.toInt()
            }
            Toast.makeText(this, engine?.opponentRating.toString(), Toast.LENGTH_LONG).show()
            button_player_1_online_xog.text = "$yourName (${engine?.userRating})"
            button_player_2_online_xog.text = "$opponentsName (${engine?.opponentRating})"
            engine?.init()
            signature_canvas.engine = engine
            signature_canvas.username = yourName
        } else {
            engineLong = object : LongGameEngine {
                override val userT = timer2_xog_online
                override val opponentT = timer_xog_online
                override val user = yourName
                override val opponent = opponentsName
                override var move = intent.getStringExtra("move") == "1"
                override var positionData = gameData
                override var activity: Activity = this@XOGameActivity
                override var type = "XOGame"
                override var key = intent.getStringExtra("key")
            }
            Toast.makeText(this, engineLong?.key.toString(), Toast.LENGTH_LONG).show()
            engineLong?.init()
        }
        var initialMove = intent.getStringExtra("move") == "1"
        signature_canvas.username = yourName
        signature_canvas.isFirstMove = intent.getStringExtra("move") == "1"
        gameData.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                var cnt = 0
                if (p0.childrenCount >= 2) {
                    engine?.changeMoveAndSyncTimer(p0)
                }
                for (i in 0..6) {
                    for (j in 0..5) {
                        val p = p0.child("FIELD").child("$i").child("$j")
                        if (p.exists()) {
                            cnt++
                            signature_canvas.FIELD[i][j] = p.value.toString().toInt()
                            if(helpend_var != 0)
                            {
                                helpend_var++
                            }
                            else
                            {
                                if(SOUND)
                                {
                                    mSound.play(1,1F,1F,1,0,1F)
                                }
                                if(VIBRATION)
                                {
                                    vibratorService?.vibrate(70)
                                }
                            }
                            var flag: Boolean = true
                            val prfs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                            if (prfs?.getString(
                                    gameData.toString() + "xog_game_history",
                                    "0"
                                ) != "0"
                            ) {
                                signature_canvas.History =
                                    prfs?.getString(gameData.toString() + "xog_game_history", "a")
                                        ?.let { decode(it) }!!
                            }
                            for (kol in 0 until signature_canvas.History.size) {
                                if (i == signature_canvas.History[kol].first && j == signature_canvas.History[kol].second) {
                                    flag = false
                                }
                            }
                            if (flag) {
                                signature_canvas. History.add(Triple(i, j,signature_canvas.FIELD[i][j]))
                                var data_from_memory = encode(signature_canvas.History)
                                val editor =
                                    getSharedPreferences("UserData", Context.MODE_PRIVATE)
                                        .edit()
                                editor.putString(
                                    gameData.toString() + "xog_game_history",
                                    data_from_memory
                                )
                                editor.apply()
                            }
                        }
                    }
                }
                fun checkForWin(): MutableList<Int> {
                    val field = signature_canvas.FIELD
                    val list_x = mutableListOf(1, 1, 0, -1)
                    val list_y = mutableListOf(0, 1, 1, 1)

                    val ans = mutableListOf(0)
                    for (i in 0..6) {
                        for (j in 0..5) {
                            if (field[i][j] != 0) {
                                for (k in 0..3) {
                                    var fl = 0
                                    for (pos in 0..2) {
                                        Log.w("TAG", "$i ${list_x[k]} $pos")
                                        if (field[(i + list_x[k] * pos + 7) % 7][(j + list_y[k] * pos + 6) % 6] != field[(i + list_x[k] * (pos + 1) + 7) % 7][(j + list_y[k] * (pos + 1) + 6) % 6]) {
                                            fl = 1

                                        }
                                    }
                                    if (fl == 0) {
                                        for (pos in 0..3) {
                                            ans.add((i + list_x[k] * pos + 7) % 7)
                                            ans.add((j + list_y[k] * pos + 6) % 6)
                                        }
                                        return ans
                                    }
                                }
                            }
                        }
                    }
                    return ans
                }
                if (signature_canvas.isFirstMove == (cnt % 2 == 0)) signature_canvas.blocked = false
                signature_canvas.invalidate()
                val checkList = checkForWin()
                Toast.makeText(this@XOGameActivity, engine?.move.toString(), Toast.LENGTH_LONG).show()
                if (p0.hasChild("winner") || checkList.size > 1 || (checkList.size == 1 && cnt == 42)) {
                    gameData.child("FIELD").child("result").onDisconnect().cancel()
                    engine?.stopTimer()
                    signature_canvas.blocked = true
                    var whoWins = 0
                    if (!p0.child("FIELD").hasChild("result")) {
//                    Toast.makeText(applicationContext,"${signature_canvas.FIELD[checkList[1]][checkList[2]]}", Toast.LENGTH_LONG).show()
                        if (checkList.size > 1) {
                            for (i2 in 0..8) {
                                if (i2 % 2 == 1) {
                                    whoWins =
                                        signature_canvas.FIELD[checkList.get(i2)][checkList.get(i2 + 1)]
                                    signature_canvas.invalidate()
                                }
                            }
                        }
                    }
                    var res: String
                    if (checkList.size == 1) {
                        res = "Ничья"
                    } else {
                        if (initialMove == (yourName < opponentsName_)) {
                            res = if (yu == '0') {
                                if (whoWins == 1) {
                                    "Победа"
                                } else {
                                    "Поражение"
                                }
                            } else {
                                if (whoWins == 2) {
                                    "Победа"
                                } else {
                                    "Поражение"
                                }
                            }
                        } else {
                            res = if (yu == '1') {
                                if (whoWins == 1) {
                                    "Победа"
                                } else {
                                    "Поражение"
                                }
                            } else {
                                if (whoWins == 2) {
                                    "Победа"
                                } else {
                                    "Поражение"
                                }
                            }
                        }
                    }
                    if (p0.hasChild("winner") && p0.child("winner").value.toString() == yourName) {
                        res = "Победа"
                    }
                    if (p0.hasChild("winner") && p0.child("winner").value.toString() == opponentsName) {
                        res = "Поражение"
                    }
                    engine?.finish(res, this@XOGameActivity, isRun)
                    engineLong?.finish(res, this@XOGameActivity, isRun)
                    gameData.removeEventListener(this)
                }
            }
        })
        if(Design == "Noir" ) {
            label_online.setBackgroundResource(R.drawable.background_noir);
        }


        DDD = Dialog(this)
        DDD.setContentView(R.layout.activity_game_over)
        adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forUnifiedNativeAd { unifiedNativeAd : UnifiedNativeAd ->
                // Show the ad.

                val adView = this.layoutInflater
                    .inflate(R.layout.natative_ads, null) as UnifiedNativeAdView
                populateUnifiedNativeAdView(unifiedNativeAd, adView)
                if (this.isDestroyed) {
                    unifiedNativeAd.destroy()
                    return@forUnifiedNativeAd
                }

            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    // Handle the failure by logging, altering the UI, and so on.
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    // Methods in the NativeAdOptions.Builder class can be
                    // used here to specify individual options settings.
                    .build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

    }

    override fun onPause() {
        super.onPause()
        isRun = false
        engine?.finish("Поражение", this, isRun)
        finish()
    }

    override fun onDestroy() {
        val editor = getSharedPreferences("UserData", Context.MODE_PRIVATE).edit()
        //editor.putString(gameData.toString() + "snake_game_history", null)
        editor.putString(gameData.toString() + "xog_game_history", null)
      //  editor.putString(gameData.toString() + "xog_game_history", null)
     //   editor.putString(gameData.toString() + "dot_game_history", null)
    //    editor.putString(gameData.toString() + "reversi_game_history", null)
   //     editor.putString(gameData.toString() + "box_game_history", null)
        editor.apply()
        super.onDestroy()
    }


}





class CanvasView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    var CONDITION_XOG : Int = 0;

    fun encode(h: MutableList<Triple<Int,Int,Int>>):String
    {
        var answer: String = ""
        for(i in 0 until h.size)
        {
            answer = answer + h[i].first.toString() + 'a' + h[i].second.toString() + 'a' + h[i].third.toString() + 'a'
        }
        return answer
    }
    fun string_to_int(s: String): Int
    {
        var i : Int = 0
        var k: Int = 1
        var answer: Int = 0
        while(i<s.length)
        {
            answer += (s[s.length-i-1].toInt() - '0'.toInt())*k
            k= k*10
            i++
        }
        return answer
    }
    fun decode(s : String) : MutableList<Triple<Int,Int,Int>>
    {
        var answer: MutableList<Triple<Int,Int,Int>> = mutableListOf()
        var i : Int = 0
        var a: Int = 0
        var b: Int = 0
        var c: Int = 0
        var s1: String = ""
        while(i<s.length)
        {
            s1 = ""
            while(i < s.length && s[i]!='a')
            {
                s1+=s[i]
                i++
            }
            a = string_to_int(s1)
            s1 = ""
            i++
            while(i < s.length && s[i]!='a')
            {
                s1+=s[i]
                i++
            }
            b = string_to_int(s1)
            s1 = ""
            i++
            while(i < s.length && s[i]!='a')
            {
                s1+=s[i]
                i++
            }
            c = string_to_int(s1)
            answer.add(Triple(a,b,c))
            i++
        }
        return answer
    }

    var username: String = ""

    var engine: BlitzGameEngine? = null

    fun touch_refinement_X (indent: Float,x : Float,width1: Float,size_field_x1:Int ):Float        //уточняет касания по оси x
    {
        if(x<indent)
        {
            return -1f
        }
        if(x>width1-indent)
        {
            return -1f
        }
        var a : Float = indent
        while(x>a)
        {
            a+=step
        }
        return a - step
    }

    fun touch_refinement_Y (indent: Float,y : Float,height1: Float,size_field_y1:Int,step: Float,advertising_line_1:Float):Float      //уточняет касания по оси Y
    {
        if(y > height1 - advertising_line_1 ||  y < height1 - advertising_line_1 - step*size_field_y1)
        {
            return -1f
        }
        var a: Float = height1 - advertising_line_1 - step*size_field_y1
        while(y>a)
        {
            a+=step
        }
        return a - step
    }

    fun touch_refinement_for_Array_X (indent: Float,x : Float,step:Float):Int        //уточняет координаты в массиве  при касании
    {
        if(x<0)
        {
            return -1
        }
        return ((x-indent)/step).toInt()
    }

    private fun touch_refinement_for_Array_Y (indent: Float, y : Float, height1: Float, size_field_y1: Int, step: Float, advertising_line_1:Float):Int      //уточняет координаты в массиве  при касании
    {
        if(y<0)
        {
            return -1
        }
        var a: Float = height1 - advertising_line_1 - step*size_field_y1
        var b :Int = 0
        while(y>a)
        {
            a+=step
            b+=1
        }
        return b-1
    }

    private fun translate_from_Array_to_Graphics_X(indent: Float,x:Int, step: Float):Float    //переводит массивные координаты в графически
    {
        return x*step+indent
    }

    fun translate_from_Array_to_Graphics_Y(indent: Float,y:Int,height1: Float,size_field_y1: Int,step: Float,advertising_line_1: Float):Float    //переводит массивные координаты в графически
    {
        return y*step + height1 - size_field_y1*step - advertising_line_1
    }
    fun checkForWin_another_fun(): MutableList<Int> {
        val list_x = mutableListOf(1, 1, 0, -1)
        val list_y = mutableListOf(0, 1, 1, 1)

        var ans = mutableListOf(0)
        for (i in 0..6) {
            for (j in 0..5) {
                if (FIELD[i][j] != 0) {
                    for (k in 0..3) {
                        var fl = 0
                        for (pos in 0..2) {
                            Log.w("TAG", "$i ${list_x[k]} $pos")
                            if (FIELD[(i + list_x[k] * pos + 7) % 7][(j + list_y[k] * pos + 6) % 6] != FIELD[(i + list_x[k] * (pos + 1) + 7) % 7][(j + list_y[k] * (pos + 1) + 6) % 6]) {
                                fl = 1

                            }
                        }
                        if (fl == 0) {
                            for (pos in 0..3) {
                                ans.add((i + list_x[k] * pos + 7) % 7)
                                ans.add((j + list_y[k] * pos + 6) % 6)
                            }
                            return ans
                        }
                    }
                }
            }
        }
        return ans
    }

    lateinit var positionData: DatabaseReference
    var isFirstMove = false
    var blocked = true
    var Exit : Int
    var circlex : Float = 0f   //координаты нажатия
    var circley : Float = 0f

    var indent : Float = 20f

    var paint : Paint = Paint()          //ресурсы для рисования
    var Line_paint: Paint = Paint()
    var Line_paint_1: Paint = Paint()
    var line_who_do_move : Paint = Paint()
    var FIELD = Array(7){IntArray(6)}

    var History: MutableList<Triple<Int,Int,Int>> = mutableListOf()
    var CLONE_FIELD = Array(7){IntArray(6)}

    var cross_or_nul: String
    var step: Float = 0f


    init {
        Exit = 0

        Line_paint.color = Color.BLACK          //ресур для линий (ширина и цвет)
        Line_paint.strokeWidth = 10f

        Line_paint_1.color = Color.BLACK          //ресур для линий (ширина и цвет)
        Line_paint_1.strokeWidth = 20f
        line_who_do_move.strokeWidth = 7f//не заполненный

        //не заполненный
        when (Design) {
            "Normal" -> {
                line_who_do_move.color =  Color.GREEN
                line_who_do_move.strokeWidth = 14f
                Line_paint.setColor(Color.rgb(217, 217, 217))          //ресур для линий (ширина и цвет)
                Line_paint.setStrokeWidth(7f)
            }
            "Egypt" -> {
                Line_paint.color = Color.BLACK          //ресур для линий (ширина и цвет)
                Line_paint.strokeWidth = 7f
                line_who_do_move.color = Color.RED

            }
            "Casino" -> {
                Line_paint.color = Color.WHITE          //ресур для линий (ширина и цвет)
                Line_paint.strokeWidth = 7f
                line_who_do_move.color = Color.WHITE             //

            }
            "Rome" -> {
                Line_paint.color = Color.rgb(193, 150, 63)    //ресур для линий (ширина и цвет)
                Line_paint.strokeWidth = 7f
                line_who_do_move.color = Color.BLACK         //

            }
            "Gothic" -> {
                Line_paint.color = Color.rgb(100, 100, 100)   //ресур для линий (ширина и цвет)
                Line_paint.strokeWidth = 7f
                line_who_do_move.color = Color.WHITE              //

            }
            "Japan" -> {
                Line_paint.color = Color.BLACK   //ресур для линий (ширина и цвет)
                Line_paint.strokeWidth = 7f
                line_who_do_move.color = Color.RED              //

            }
            "Noir" -> {
                Line_paint.color = Color.rgb(100, 100, 100)   //ресур для линий (ширина и цвет)
                Line_paint.strokeWidth = 7f
                line_who_do_move.color = Color.RED              //

            }


            // TODO нужно взять из DataBase (статистика ходов)
        }


        // TODO нужно взять из DataBase (статистика ходов)
        for( i in 0..6) {
            for(j in 0 ..5) {
                FIELD[i][j] = 0 //не заполненный
            }
        }
        cross_or_nul  = "cross"

        // TODO нужно взять из DataBase (статистика ходов)
        for( i in 0..6) {
            for(j in 0 ..5) {
                FIELD[i][j] = 0 //не заполненный
                CLONE_FIELD[i][j] = 0;
            }
        }
        cross_or_nul  = "cross"
    }

    private fun moveChecker(x: Int, y: Int, cnt: Int): Boolean {
        if (cnt % 2 == 0 != isFirstMove) {
            return false
        }
        if (x > 6 || x < 0 || y > 5 || y < 0 || (y + 1 <= 5 && FIELD[x][y + 1] == 0)) {
            return false
        }
        return true
    }




    var cross_normal : Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cross_normal)       //картинки крестиков и нулей
    var null_normal: Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.null_normal)

    var cross_egypt : Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cross_egypt)       //картинки крестиков и нулей
    var null_egypt: Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle_egypt)

    var cross_casino : Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cross_casino)       //картинки крестиков и нулей
    var null_casino: Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.null_casino)

    var cross_rome : Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cross_rome)       //картинки крестиков и нулей
    var null_rome: Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.null_rome)

    var cross_gothic : Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cross_gothic)       //картинки крестиков и нулей
    var null_gothic: Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.null_gothic)

    var cross_japan : Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cross_japan)       //картинки крестиков и нулей
    var null_japan: Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.null_japan)

    var cross_noir : Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cross_noir)       //картинки крестиков и нулей
    var null_noir: Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.null_noir)

    // var BackgroundColor_Egypt: Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_egypt)
    var icon_green : Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.illumination)
    var icon_grenn_Egypt : Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.illumination)



    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        indent = 20f
        val width = getWidth().toFloat()
        val height = getHeight().toFloat()
        //ширина и высота экрана (от ширины в основном все зависит)

        val size_field_x: Int = 7
        val size_field_y: Int = 6
        step = (width-2*indent)/size_field_x
        val advertising_line: Float = (height - step * 6) / 2

        var k = height - (width-2*indent)*size_field_y/size_field_x - advertising_line
        for(i in 0 until 7)          //вырисовка горизонтальных линий
        {
            canvas?.drawLine(indent,k,width-indent,k,Line_paint)
            k += step
        }
        k  =  height-(width-2*indent)*size_field_y/size_field_x-advertising_line
        var t = indent
        for(i in 0 until 8)         //вырисовка вертикальных линий
        {
            canvas?.drawLine(t,k,t,height-advertising_line,Line_paint)
            t += step
        }


        var right_cross: Bitmap  //подгоняем картинку под размеры экрана телефона
        var right_null: Bitmap
        var right_green: Bitmap

        right_cross = Bitmap.createScaledBitmap(cross_normal,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
        right_null = Bitmap.createScaledBitmap(null_normal,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
        right_green = Bitmap.createScaledBitmap(icon_green, (width.toInt() - 2 * indent.toInt()) / size_field_x, (width.toInt() - 2 * indent.toInt()) / size_field_x, true)
        when (Design) {
            "Egypt" -> {
                right_cross = Bitmap.createScaledBitmap(cross_egypt,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
                right_null = Bitmap.createScaledBitmap(null_egypt,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
                right_green = Bitmap.createScaledBitmap(icon_grenn_Egypt, (width.toInt() - 2 * indent.toInt()) / size_field_x, (width.toInt() - 2 * indent.toInt()) / size_field_x, true)
            }
            "Casino" -> {
                right_cross = Bitmap.createScaledBitmap(cross_casino,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
                right_null = Bitmap.createScaledBitmap(null_casino,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
            }
            "Rome" -> {
                right_cross = Bitmap.createScaledBitmap(cross_rome,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
                right_null = Bitmap.createScaledBitmap(null_rome,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
            }
            "Gothic" -> {
                right_cross = Bitmap.createScaledBitmap(cross_gothic,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
                right_null = Bitmap.createScaledBitmap(null_gothic,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
            }
            "Japan" -> {
                right_cross = Bitmap.createScaledBitmap(cross_japan,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
                right_null = Bitmap.createScaledBitmap(null_japan,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
            }
            "Noir" -> {
                right_cross = Bitmap.createScaledBitmap(cross_noir,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
                right_null = Bitmap.createScaledBitmap(null_noir,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
            }
        }

        if(CONDITION_XOG == 0) {
            var cnt = 0
            for (i in 0..6) //начальная расстановка крестиков и ноликов
            {
                for (j in 0..5) {
                    if (FIELD[i][j] == 1)  //крестик
                    {
                        cnt++
                        canvas?.drawBitmap(
                            right_cross, translate_from_Array_to_Graphics_X(indent, i, step),
                            translate_from_Array_to_Graphics_Y(
                                indent,
                                j,
                                height,
                                size_field_y,
                                step,
                                advertising_line
                            ), paint
                        )
                    }
                    if (FIELD[i][j] == 2)  //нолик
                    {
                        cnt--
                        canvas?.drawBitmap(
                            right_null, translate_from_Array_to_Graphics_X(indent, i, step),
                            translate_from_Array_to_Graphics_Y(
                                indent,
                                j,
                                height,
                                size_field_y,
                                step,
                                advertising_line
                            ), paint
                        )
                    }
                }
            }

            cross_or_nul = if (cnt == 0) {
                "cross"
            } else {
                "null"
            }

            if (touch_refinement_Y(
                    indent,
                    circley,
                    height,
                    size_field_y,
                    step,
                    advertising_line
                ) > 0
            )     //постановка нового обЪекта
            {
                if(SOUND)
                {
                    mSound.play(1,1F,1F,1,0,1F)
                }
                if(VIBRATION)
                {
                    vibratorService?.vibrate(70)
                }
                val X: Int = touch_refinement_for_Array_X(indent, circlex, step)
                val Y: Int = touch_refinement_for_Array_Y(
                    indent,
                    circley,
                    height,
                    size_field_y,
                    step,
                    advertising_line
                )    //координаты нажимаего для массива

                if (moveChecker(X, Y, cnt) && FIELD[X][Y] == 0) {
                    blocked = true
                    var a: Float = circlex
                    var b: Float = circley
                    if (cross_or_nul == "cross") {
                        canvas?.drawBitmap(
                            right_cross, touch_refinement_X(indent, a, width, size_field_x),
                            touch_refinement_Y(
                                indent,
                                b,
                                height,
                                size_field_y,
                                step,
                                advertising_line
                            ), paint
                        )
                        FIELD[X][Y] = 1
                        var flag: Boolean = true
                        val prfs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
                        if (prfs?.getString(
                                positionData.toString() + "xog_game_history",
                                "0"
                            ) != "0"
                        ) {
                            History =
                                prfs?.getString(positionData.toString() + "xog_game_history", "a")
                                    ?.let { decode(it) }!!
                        }
                        for (kol in 0 until History.size) {
                            if (X == History[kol].first && Y == History[kol].second) {
                                flag = false
                            }
                        }
                        if (flag) {
                            History.add(Triple(X, Y, FIELD[X][Y]))
                            var data_from_memory = encode(History)
                            val editor =
                                context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
                                    .edit()
                            editor.putString(
                                positionData.toString() + "xog_game_history",
                                data_from_memory
                            )
                            editor.apply()
                        }
                        val upd = mapOf(
                            "/FIELD/$X/$Y" to 1,
                            "/time/$username/" to engine?.cntUser.toString()
                        )
                        positionData.updateChildren(upd)
                        cross_or_nul = "null"
                        Exit = 1
                    } else {
                        canvas?.drawBitmap(
                            right_null, touch_refinement_X(indent, a, width, size_field_x),
                            touch_refinement_Y(
                                indent,
                                b,
                                height,
                                size_field_y,
                                step,
                                advertising_line
                            ), paint
                        )
                        FIELD[X][Y] = 2
                        var flag: Boolean = true
                        val prfs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
                        if (prfs?.getString(
                                positionData.toString() + "xog_game_history",
                                "0"
                            ) != "0"
                        ) {
                            History =
                                prfs?.getString(positionData.toString() + "xog_game_history", "a")
                                    ?.let { decode(it) }!!
                        }
                        for (kol in 0 until History.size) {
                            if (X == History[kol].first && Y == History[kol].second) {
                                flag = false
                            }
                        }
                        if (flag) {
                            History.add(Triple(X, Y, FIELD[X][Y]))
                            var data_from_memory = encode(History)
                            val editor =
                                context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
                                    .edit()
                            editor.putString(
                                positionData.toString() + "xog_game_history",
                                data_from_memory
                            )
                            editor.apply()
                        }
                        val upd = mapOf(
                            "/FIELD/$X/$Y" to 2,
                            "/time/$username/" to engine?.cntUser.toString()
                        )
                        positionData.updateChildren(upd)
                        cross_or_nul = "cross"
                    }
                }
            }

            if (checkForWin_another_fun().size == 9) {
                var counter: Int = 1
                while (counter < 9) {
                    var a_1: Float = translate_from_Array_to_Graphics_X(
                        indent,
                        checkForWin_another_fun()[counter],
                        step
                    )


                    var a_2: Float = translate_from_Array_to_Graphics_Y(
                        indent,
                        checkForWin_another_fun()[counter + 1],
                        height,
                        size_field_y,
                        step,
                        advertising_line
                    )
                    canvas?.drawBitmap(right_green, a_1, a_2, paint)
                    counter += 2
                }
            }
        }
        else
        {
            val prfs = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
            History = prfs?.getString(positionData.toString()+"xog_game_history", "0")?.let { decode(it) }!!
            for(i in 0 until CLONE_FIELD.size)
            {
                for(j in 0 until CLONE_FIELD[0].size)
                {
                    CLONE_FIELD[i][j] = 0;
                }
            }

            if(CONDITION_XOG>History.size)
            {
                CONDITION_XOG = History.size
            }
            for(q in 0 until History.size - CONDITION_XOG)
            {
                var i = History[q].first
                var j = History[q].second
                CLONE_FIELD[i][j] = FIELD[i][j]
            }

            var cnt_clone= 0 ;
            for (i in 0..6) //начальная расстановка крестиков и ноликов
            {
                for (j in 0..5) {
                    if (CLONE_FIELD[i][j] == 1)  //крестик
                    {
                        cnt_clone++
                        canvas?.drawBitmap(
                            right_cross, translate_from_Array_to_Graphics_X(indent, i, step),
                            translate_from_Array_to_Graphics_Y(
                                indent,
                                j,
                                height,
                                size_field_y,
                                step,
                                advertising_line
                            ), paint
                        )
                    }
                    if (CLONE_FIELD[i][j] == 2)  //нолик
                    {
                        cnt_clone--
                        canvas?.drawBitmap(
                            right_null, translate_from_Array_to_Graphics_X(indent, i, step),
                            translate_from_Array_to_Graphics_Y(
                                indent,
                                j,
                                height,
                                size_field_y,
                                step,
                                advertising_line
                            ), paint
                        )
                    }
                }
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (blocked) {
            return false
        }
        if(CONDITION_XOG!=0)
        {
            return false
        }
        super.onTouchEvent(event)
        circlex =  event!!.x
        circley =  event!!.y
        invalidate()
        return true
    }


}
