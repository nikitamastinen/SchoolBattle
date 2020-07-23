package com.example.schoolbattle.gamesonline

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.example.schoolbattle.*
import com.example.schoolbattle.R
import com.google.firebase.database.*
import com.instacart.library.truetime.TrueTime
import kotlinx.android.synthetic.main.activity_x_o_game.*
import java.util.*
import android.graphics.Color.*
import android.widget.TextView
import com.example.schoolbattle.engine.BlitzGameEngine
import com.example.schoolbattle.engine.ShowResult
import com.example.schoolbattle.engine.StupidGame


class XOGameActivity : AppCompatActivity() {
    private var isRun = false
    private var dialog: ShowResult? = null
    private var engine: BlitzGameEngine? = null

    override fun onResume() {
        super.onResume()
        currentContext = this
        isRun = true
        CONTEXT = this
    }

    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        setContentView(R.layout.activity_online_games_temlate)
        signature_canvas.visibility = View.VISIBLE

        if(Design == "Egypt" ) {
            button_player_1_online_xog.setTextColor(BLACK)
            button_player_2_online_xog.setTextColor(BLACK)
            button_player_1_online_xog.textSize = 20f
            button_player_2_online_xog.textSize = 20f
            timer_xog_online.textSize = 15f
            timer_xog_online.setTextColor(GREEN)
            timer2_xog_online.textSize = 15f
            timer2_xog_online.setTextColor(GREEN)

            icon_player_1_xog_online.setBackgroundResource(R.drawable.player1_egypt);
            icon_player_2_xog_online.setBackgroundResource(R.drawable.player2_egypt);
            player_1_icon_xog_online.setBackgroundResource(R.drawable.cross_egypt);
            player_2_icon_xog_online.setBackgroundResource(R.drawable.circle_egypt);
            label_online_xog.setBackgroundResource(R.drawable.background_egypt);
            bottom_navigation_xog_online.setBackgroundColor(rgb(224,164,103))
            to_back_xog_online.setBackgroundResource(R.drawable.arrow_back)
            toolbar_xog_online.setBackgroundColor(argb(0,0,0,0))
            toolbar2_xog_online.setBackgroundColor(argb(0,0,0,0))
        }

        currentContext = this
        CONTEXT = this
        isRun = true

        if (StupidGame != Activity()) StupidGame.finish()
        if (NewGame != Activity()) NewGame.finish()
        val yourName =
            getSharedPreferences("UserData", Context.MODE_PRIVATE).getString("username", "")
                .toString()

        var type = intent.getStringExtra("type")
        if (type == null) type = ""
        val opponentsName_: String = intent?.getStringExtra("opponent").toString()
        var opponentsName = ""
        for (i in opponentsName_) {
            if (i == ' ') break
            opponentsName += i
        }
        val yu = if (opponentsName < yourName) '1' else '0'
        val op = if (opponentsName < yourName) '0' else '1'
        val gameData = myRef.child(type + "XOGames").child(
            if (opponentsName < yourName)
                opponentsName + '_' + yourName else yourName + '_' + opponentsName
        )
        signature_canvas.blocked = true
        signature_canvas.positionData = gameData

        button_player_1_online_xog.text = yourName
        button_player_2_online_xog.text = opponentsName
        if(Design == "Egypt" ) {
            button_player_1_online_xog.setTextColor(BLACK)
            button_player_2_online_xog.setTextColor(BLACK)
            button_player_1_online_xog.textSize = 20f
            button_player_2_online_xog.textSize = 20f
            timer_xog_online.setTextSize(15f)
            timer_xog_online.setTextColor(GREEN)
            timer2_xog_online.setTextSize(15f)
            timer2_xog_online.setTextColor(GREEN)

            icon_player_1_xog_online.setBackgroundResource(R.drawable.player1_egypt);
            icon_player_2_xog_online.setBackgroundResource(R.drawable.player2_egypt);
            player_1_icon_xog_online.setBackgroundResource(R.drawable.cross_egypt);
            player_2_icon_xog_online.setBackgroundResource(R.drawable.circle_egypt);
            label_online_xog.setBackgroundResource(R.drawable.background_egypt);
            bottom_navigation_xog_online.setBackgroundColor(rgb(224, 164, 103))
            to_back_xog_online.setBackgroundResource(R.drawable.arrow_back)
            toolbar_xog_online.setBackgroundColor(argb(0, 0, 0, 0))
            toolbar2_xog_online.setBackgroundColor(argb(0, 0, 0, 0))
        }


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
            }
            engine?.init()
            signature_canvas.engine = engine
            signature_canvas.username = yourName
        }
        signature_canvas.isFirstMove = intent.getStringExtra("move") == "1"
        gameData.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                var cnt = 0
                if (p0.hasChildren()) {
                    engine?.changeMoveAndSyncTimer(p0)
                }
                for (i in 0..6) {
                    for (j in 0..5) {
                        val p = p0.child("FIELD").child("$i").child("$j")
                        if (p.exists()) {
                            cnt++
                            signature_canvas.FIELD[i][j] = p.value.toString().toInt()
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
                if (checkList.size > 1 || (checkList.size == 1 && cnt == 42)) {
                    engine?.stopTimer()
                    signature_canvas.blocked = true
                    Toast.makeText(applicationContext,"${signature_canvas.FIELD[checkList[1]][checkList[2]]}", Toast.LENGTH_LONG).show()
                    var whoWins = 0
                    if (checkList.size > 1) {
                        for (i2 in 0..8) {
                            if (i2 % 2 == 1) {
                                whoWins = signature_canvas.FIELD[checkList.get(i2)][checkList.get(i2 + 1)]
                                signature_canvas.invalidate()
                            }
                        }
                    }
                    val res: String
                    if (checkList.size == 1) {
                        res = "Ничья"
                    } else {
                        if (p0.child("Move").value.toString() == "0") {
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
                    myRef.child(type + "XOGames").child(if (opponentsName < yourName)
                        opponentsName + '_' + yourName else yourName + '_' + opponentsName
                    ).removeValue()


                    myRef.child("Users").child(yourName).child(type + "Games").child("$opponentsName XOGame").removeValue()
                    myRef.child("Users").child(opponentsName).child(type + "Games").child("$yourName XOGame").removeValue()
                    dialog =
                        ShowResult(this@XOGameActivity)
                    if (isRun) {
                        dialog?.showResult(res, "XOGame", yourName, opponentsName)
                    }
                    gameData.removeEventListener(this)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        engine?.stopTimer()
        isRun = false
        currentContext = null
        if (dialog != null) {
            dialog?.delete()
            finish()
        } else {
            Log.w("HHH", "NONULL")
        }
        finish()
    }
}





class CanvasView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

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

    fun touch_refinement_for_Array_Y (indent: Float,y : Float,height1: Float,size_field_y1: Int,step: Float,advertising_line_1:Float):Int      //уточняет координаты в массиве  при касании
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
    var FIELD = Array(7){IntArray(6)}
    var cross_or_nul: String
    var step: Float = 0f

    init {
        Exit = 0
        Line_paint.color = RED          //ресур для линий (ширина и цвет)
        Line_paint.strokeWidth = 10f

        Line_paint_1.color = RED          //ресур для линий (ширина и цвет)
        Line_paint_1.strokeWidth = 20f

        // TODO нужно взять из DataBase (статистика ходов)
        for( i in 0..6) {
            for(j in 0 ..5) {
                FIELD[i][j] = 0 //не заполненный
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




    var icon_cross_egypt : Bitmap = BitmapFactory.decodeResource(context.getResources(),
        R.drawable.cross_egypt
    )       //картинки крестиков и нулей
    var icon_null_egypt: Bitmap = BitmapFactory.decodeResource(context.getResources(),
        R.drawable.circle_egypt
    )

    // var BackgroundColor_Egypt: Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_egypt)
    var icon_green : Bitmap = BitmapFactory.decodeResource(context.getResources(),
        R.drawable.illumination
    )
    var icon_grenn_Egypt : Bitmap = BitmapFactory.decodeResource(context.getResources(),
        R.drawable.ram_egypt_xog
    )


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


        val right_icon_cross: Bitmap  //подгоняем картинку под размеры экрана телефона
        val right_icon_null: Bitmap
        val right_icon_green: Bitmap
        if(Design == "Egypt")
        {
            right_icon_cross = Bitmap.createScaledBitmap(icon_cross_egypt,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
            right_icon_null = Bitmap.createScaledBitmap(icon_null_egypt,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
            right_icon_green = Bitmap.createScaledBitmap(
                icon_grenn_Egypt,
                (width.toInt() - 2 * indent.toInt()) / size_field_x,
                (width.toInt() - 2 * indent.toInt()) / size_field_x,
                true
            )
        }
        else {
            right_icon_cross = Bitmap.createScaledBitmap(icon_cross_egypt,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
            right_icon_null = Bitmap.createScaledBitmap(icon_null_egypt,(width.toInt()-2*indent.toInt())/size_field_x, (width.toInt()-2*indent.toInt())/size_field_x, true);
            right_icon_green = Bitmap.createScaledBitmap(
                icon_green,
                (width.toInt() - 2 * indent.toInt()) / size_field_x,
                (width.toInt() - 2 * indent.toInt()) / size_field_x,
                true
            )
        }

        var cnt = 0
        for( i in 0..6) //начальная расстановка крестиков и ноликов
        {
            for(j in 0..5) {
                if (FIELD[i][j] == 1)  //крестик
                {
                    cnt++
                    canvas?.drawBitmap(right_icon_cross, translate_from_Array_to_Graphics_X(indent,i,step),
                        translate_from_Array_to_Graphics_Y(indent,j,height,size_field_y,step,advertising_line),paint)
                }
                if (FIELD[i][j] == 2)  //нолик
                {
                    cnt--
                    canvas?.drawBitmap(right_icon_null, translate_from_Array_to_Graphics_X(indent,i,step),
                        translate_from_Array_to_Graphics_Y(indent,j,height,size_field_y,step,advertising_line),paint)
                }
            }
        }

        cross_or_nul = if (cnt == 0) {
            "cross"
        } else {
            "null"
        }

        if (touch_refinement_Y(indent,circley,height,size_field_y,step,advertising_line)>0)     //постановка нового обЪекта
        {
            val X: Int = touch_refinement_for_Array_X(indent,circlex,step)
            val Y: Int = touch_refinement_for_Array_Y(indent,circley,height,size_field_y,step,advertising_line)    //координаты нажимаего для массива

            if (moveChecker(X, Y, cnt) && FIELD[X][Y]==0)
            {
                blocked = true
                var a:Float = circlex
                var b:Float = circley
                if(cross_or_nul=="cross")
                {
                    canvas?.drawBitmap(right_icon_cross,touch_refinement_X(indent,a,width,size_field_x),
                        touch_refinement_Y(indent,b,height,size_field_y,step,advertising_line),paint)
                    FIELD[X][Y] = 1
                    val upd = mapOf(
                        "/FIELD/$X/$Y" to 1,
                        "/time/$username/" to engine?.cntUser.toString()
                    )
                    positionData.updateChildren(upd)
                    cross_or_nul = "null"
                    Exit = 1
                }
                else
                {
                    canvas?.drawBitmap(right_icon_null,touch_refinement_X(indent,a,width,size_field_x),
                        touch_refinement_Y(indent,b,height,size_field_y,step,advertising_line),paint)
                    FIELD[X][Y] = 2
                    val upd = mapOf(
                        "/FIELD/$X/$Y" to 2,
                        "/time/$username/" to engine?.cntUser.toString()
                    )
                    positionData.updateChildren(upd)
                    cross_or_nul = "cross"
                }
            }
        }

        if(checkForWin_another_fun().size==9)
        {
            var counter: Int = 1
            while(counter<9)
            {
                var a_1: Float =  translate_from_Array_to_Graphics_X(indent,checkForWin_another_fun()[counter],step)


                var a_2: Float = translate_from_Array_to_Graphics_Y(indent,
                    checkForWin_another_fun()[counter+1], height,size_field_y, step, advertising_line)
                canvas?.drawBitmap(right_icon_green,a_1,a_2,paint)
                counter += 2
            }
        }


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (blocked) {
            return false
        }
        super.onTouchEvent(event)
        circlex =  event!!.x
        circley =  event!!.y
        invalidate()
        return true
    }


}