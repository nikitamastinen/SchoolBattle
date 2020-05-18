package com.example.schoolbattle

import android.app.Activity
import android.content.Context
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_x_o_game.*

class XOGameActivity : AppCompatActivity() {
    private var isRun = false
    private var dialog: ShowResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        dialog = ShowResult(this)
        currentContext = this
        isRun = true
        super.onCreate(savedInstanceState)
        if (StupidGame != Activity()) StupidGame.finish()
        if (NewGame != Activity()) NewGame.finish()
        val yourName =
            getSharedPreferences("UserData", Context.MODE_PRIVATE).getString("username", "")
                .toString()
        var opponentsName_: String = intent.getStringExtra("opponentName").toString()
        var opponentsName = ""
        for (i in opponentsName_) {
            if (i == ' ') break
            opponentsName += i
        }
        val yu = if (opponentsName < yourName) '1' else '0'
        val op = if (opponentsName < yourName) '0' else '1'
//        players.text = yourName + " VS " + opponentsName
  //      youName.text = yourName
    //    opponentName.text = opponentsName


        val gameData = myRef.child("XOGames").child(
            if (opponentsName < yourName)
                opponentsName + '_' + yourName else yourName + '_' + opponentsName
        )
        setContentView(R.layout.activity_x_o_game)
        signature_canvas.blocked = true
        signature_canvas.positionData = gameData

        gameData.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                var cnt = 0

                signature_canvas.isFirstMove = (p0.child("Move").value.toString() == yu.toString())
                for (i in 0..6) {
                    for (j in 0..5) {
                        val p = p0.child("$i").child("$j")
                        if (p.exists()) {
                            cnt++
                            signature_canvas.FIELD[i][j] = p.value.toString().toInt()
                        }
                    }
                }
                if (signature_canvas.isFirstMove == (cnt % 2 == 0)) signature_canvas.blocked = false
                signature_canvas.invalidate()
                if (cnt == 42) {
                    var res = "Тестовое состояние"
                    myRef.child("XOGames").child(if (opponentsName < yourName)
                        opponentsName + '_' + yourName else yourName + '_' + opponentsName
                    ).removeValue()

                    myRef.child("Users").child(yourName).child("Games").child("$opponentsName XOGame").removeValue()
                    myRef.child("Users").child(opponentsName).child("Games").child("$yourName XOGame").removeValue()
                    if (isRun) {
                        dialog?.showResult(res, "XOGame", yourName)
                    }
                    cnt = 0
                    gameData.removeEventListener(this)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        isRun = false
        currentContext = null
        dialog?.delete()
        finish()
    }
}

class CanvasView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    fun touch_refinement_X (x : Float,width1: Float,size_field_x1:Int ):Float        //уточняет касания по оси x
    {
        return ((x.toInt()/(width1/size_field_x1).toInt()).toFloat()*width1/size_field_x1).toFloat()
    }

    fun touch_refinement_Y (y : Float,height1: Float,size_field_y1:Int,step: Float,advertising_line_1:Float):Float      //уточняет касания по оси Y
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

    fun touch_refinement_for_Array_X (x : Float,step:Float):Int        //уточняет координаты в массиве  при касании
    {
        if(x<0)
        {
            return -1
        }
        return (x/step).toInt()
    }

    fun touch_refinement_for_Array_Y (y : Float,height1: Float,size_field_y1: Int,step: Float,advertising_line_1:Float):Int      //уточняет координаты в массиве  при касании
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

    private fun translate_from_Array_to_Graphics_X(x:Int, step: Float):Float    //переводит массивные координаты в графически
    {
        return x*step
    }

    fun translate_from_Array_to_Graphics_Y(y:Int,height1: Float,size_field_y1: Int,step: Float,advertising_line_1: Float):Float    //переводит массивные координаты в графически
    {
        return y*step + height1 - size_field_y1*step - advertising_line_1
    }

    lateinit var positionData: DatabaseReference
    var isFirstMove = false
    var blocked = true
    var Exit : Int
    var circlex : Float = 0f   //координаты нажатия
    var circley : Float = 0f

    var paint : Paint = Paint()          //ресурсы для рисования
    var Line_paint: Paint = Paint()
    var FIELD = Array(7){IntArray(6)}
    var cross_or_nul: String

    init {
        Exit = 0
        Line_paint.color = Color.RED          //ресур для линий (ширина и цвет)
        Line_paint.strokeWidth = 10f

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




    var icon_cross : Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cross)       //картинки крестиков и нулей
    var icon_null: Bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle_null)

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        //TODO() take field from database
        canvas?.drawColor(Color.WHITE)
        val width = getWidth().toFloat()
        val height = getHeight().toFloat()            //ширина и высота экрана (от ширины в основном все зависит)

        val advertising_line: Float = 300f
        val size_field_x: Int = 7
        val size_field_y: Int = 6


        val step: Float = width/size_field_x
        var k: Float = height-width-advertising_line + step
        for(i in 0 until size_field_x)
        {
            canvas?.drawLine(0f,k,width,k,Line_paint)
            k += step
        }
        k = 0f
        for(i in 0 until size_field_y+2)
        {
            canvas?.drawLine(k,height-advertising_line-width+step,k,height-advertising_line,Line_paint)
            k += step
        }


        val right_icon_cross: Bitmap = Bitmap.createScaledBitmap(icon_cross,width.toInt()/size_field_x, width.toInt()/size_field_x, true); //подгоняем картинку под размеры экрана телефона
        val right_icon_null: Bitmap = Bitmap.createScaledBitmap(icon_null,width.toInt()/size_field_x, width.toInt()/size_field_x, true);

        var cnt = 0
        for( i in 0..6) //начальная расстановка крестиков и ноликов
        {
            for(j in 0..5) {
                if (FIELD[i][j] == 1)  //крестик
                {
                    cnt++
                    canvas?.drawBitmap(right_icon_cross, translate_from_Array_to_Graphics_X(i,step),
                        translate_from_Array_to_Graphics_Y(j,height,size_field_y,step,advertising_line),paint)
                }
                if (FIELD[i][j] == 2)  //нолик
                {
                    cnt--
                    canvas?.drawBitmap(right_icon_null, translate_from_Array_to_Graphics_X(i,step),
                        translate_from_Array_to_Graphics_Y(j,height,size_field_y,step,advertising_line),paint)
                }
            }
        }

        cross_or_nul = if (cnt == 0) {
            "cross"
        } else {
            "null"
        }

        if (touch_refinement_Y(circley,height,size_field_y,step,advertising_line)>0)     //постановка нового обЪекта
        {
            val X: Int = touch_refinement_for_Array_X(circlex,step)
            val Y: Int = touch_refinement_for_Array_Y(circley,height,size_field_y,step,advertising_line)    //координаты нажимаего для массива

            if (moveChecker(X, Y, cnt) && FIELD[X][Y]==0)
            {
                blocked = true
                var a:Float = circlex
                var b:Float = circley
                if(cross_or_nul=="cross")
                {
                    canvas?.drawBitmap(right_icon_cross,touch_refinement_X(a,width,size_field_x),
                        touch_refinement_Y(b,height,size_field_y,step,advertising_line),paint)
                    FIELD[X][Y] = 1
                    positionData.child("$X").child("$Y").setValue(1)
                    cross_or_nul = "null"
                    Exit = 1
                }
                else
                {
                    canvas?.drawBitmap(right_icon_null,touch_refinement_X(a,width,size_field_x),
                        touch_refinement_Y(b,height,size_field_y,step,advertising_line),paint)
                    FIELD[X][Y] = 2
                    positionData.child("$X").child("$Y").setValue(2)
                    //TODO setValue to database
                    cross_or_nul = "cross"
                }

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