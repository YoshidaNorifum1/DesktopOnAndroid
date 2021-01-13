package com.example.DesktopOnAndroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.room.*
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

const val APP_NAME = "com.example.DesktopOnAndroid.APP_NAME"
const val APP_PACKAGENAME = "com.example.DesktopOnAndroid.APP_PACKAGENAME"

var contextX : Float = 0F
var contextY : Float = 0F

class MainActivity : AppCompatActivity(), View.OnTouchListener, CoroutineScope{
    val REQUEST_CODE_APPLIST = 1000
    var preX : Float = 0F
    var preY : Float = 0F

    lateinit var db :AppDatabase
    lateinit var appDoa : AppsDoa
    lateinit var dispsize : Point

    private lateinit var mDetector: GestureDetectorCompat

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dispsize = Point(0,0)
        windowManager.defaultDisplay.getSize(dispsize)
        setContentView(R.layout.activity_main)
        mDetector = GestureDetectorCompat(this,MyGestureListener())

        runBlocking { initDb() }

        val constraintlayout = findViewById<ConstraintLayout>(R.id.Main_ConstraintLayout)
        constraintlayout.setOnTouchListener(this)
        registerForContextMenu(constraintlayout)
    }

    override fun finish() {
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private suspend fun initDb() {
        db = Room.databaseBuilder(this, AppDatabase::class.java, "database-name")
            .build()
        appDoa = db.appsDoa()

        val apps = async {
            appDoa.getAll()
        }.await()

        if(apps.size == 0){
            val ts = Toast.makeText(this, "Long tap screen to add new icon", Toast.LENGTH_LONG)
            ts.setGravity(Gravity.CENTER,0,0)
            ts.show()
        }
        apps.forEach{app ->
            setItemToView(app.id,app.packageName,app.topMargin,app.leftMargin)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateContextMenu( menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo? ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater:MenuInflater = menuInflater
        when(v.id){
            R.id.Main_ConstraintLayout -> {
                inflater.inflate(R.menu.main_context_menu,menu)
            }
            else -> {
                inflater.inflate(R.menu.item_context_menu,menu)
                menu.findItem(R.id.move).setOnMenuItemClickListener{
                    v.setOnTouchListener(this)
                    true
                }
                menu.findItem(R.id.delete).setOnMenuItemClickListener {
                    launch { withContext(Dispatchers.IO){
                        appDoa. deleteAppbyId(v.id)
                    } }
                    val imageview = findViewById<ImageView>(v.id)
                    imageview.setImageDrawable(null)
                    true
                }
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.add -> {
                val intent = Intent(this,AppsList_menu::class.java)
                intent.putExtra(APP_NAME,"")
                startActivityForResult(intent,REQUEST_CODE_APPLIST)
                true
            }
            R.id.move -> {
                super.onContextItemSelected(item)
            }
            else -> {
                super.onContextItemSelected(item)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_APPLIST -> {
                if(resultCode == Activity.RESULT_OK && data != null){
                    val appName = data.getStringExtra(APP_NAME)!!
                    val appPackageName = data.getStringExtra(APP_PACKAGENAME)!!
                    val marginLeft = contextX.toInt()
                    val marginTop = contextY.toInt()

                    runBlocking { async { withContext(Dispatchers.IO) {
                                appDoa.insert(Apps(0, appName, appPackageName, marginTop, marginLeft))
                            } }.await()
                    }
                    refresh()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setItemToView(viewId: Long,packageName:String?, marginTop:Int, marginLeft:Int){
        val icon : Drawable? = packageManager.getApplicationIcon(packageName.toString())
        val constraintlayout = findViewById<ConstraintLayout>(R.id.Main_ConstraintLayout)
        val imageView = ImageView(this)
        imageView.id = viewId.toInt()
        imageView.setImageDrawable(icon)
        registerForContextMenu(imageView)
        val width = 170
        val height = 170
        val params = ConstraintLayout.LayoutParams(width,height)
            .apply {
                leftMargin = marginLeft
                topMargin = marginTop
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            }
        constraintlayout.addView(imageView,params)
        imageView.setOnClickListener{
            val pm = packageManager
            var intent = pm.getLaunchIntentForPackage(packageName.toString())
            Toast.makeText(this, "${packageName.toString()}", Toast.LENGTH_SHORT).show()
            try {
                startActivity(intent)
            }catch (e : Exception){
                Toast.makeText(this, "Can't start the application", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun refresh(){
        val apps = runBlocking { async { withContext(Dispatchers.IO){
            appDoa.getAll()
        } }.await() }

        val constraintLayout = findViewById<ConstraintLayout>(R.id.Main_ConstraintLayout)
        constraintLayout.removeAllViews()
        apps.forEach{app ->
            setItemToView(app.id,app.packageName,app.topMargin,app.leftMargin)
        }
    }


    override fun onTouch(view: View, event: MotionEvent): Boolean {

        val newX = event.rawX
        val newY = event.rawY

        when(event.action){
            MotionEvent.ACTION_DOWN->{
            }
            MotionEvent.ACTION_MOVE->{
                view.parent.requestDisallowInterceptTouchEvent(true)
                if(view.id == R.id.Main_ConstraintLayout){
                }else{
                    val constraintLayout = findViewById<ConstraintLayout>(R.id.Main_ConstraintLayout)

                    if(dispsize.x - newX < dispsize.x / 10){
                        constraintLayout.scrollX += dispsize.x / 30
                        preX -= dispsize.x / 30
                    }
                    if(newX < dispsize.x / 10){
                        constraintLayout.scrollX -= dispsize.x / 30
                        preX += dispsize.x / 30
                    }
                    if(dispsize.y - newY < dispsize.y / 10){
                        constraintLayout.scrollY += dispsize.y / 30
                        preY -= dispsize.y / 30
                    }
                    if(newY < dispsize.y / 10){
                        constraintLayout.scrollY -= dispsize.y / 30
                        preY += dispsize.y / 30
                    }

                    val x = (view.left + newX - preX).toInt()
                    val y = (view.top + newY - preY).toInt()
                    view.layout(x,y,x+view.width,y+view.height)
                }
            }
            MotionEvent.ACTION_UP->{
                if(view.id != R.id.Main_ConstraintLayout){
                    view.setOnTouchListener(null)
                    async(Dispatchers.IO) {
                        val app = appDoa.getAppbyId(view.id)
                        val newapp = Apps(app.id,app.name,app.packageName,view.top,view.left)
                        appDoa.update(newapp)
                    }
                }
            }
        }
        preX = newX
        preY = newY
        return onTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (mDetector.onTouchEvent(event)){
            true
        }else{
            super.onTouchEvent(event)
        }
    }

    private inner class MyGestureListener: GestureDetector.SimpleOnGestureListener(){
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            contextX = e.x
            contextY = e.y

            val view = findViewById<ConstraintLayout>(R.id.Main_ConstraintLayout)
            registerForContextMenu(view)
            openContextMenu(view)
            unregisterForContextMenu(view)
        }

        override fun onScroll( e1: MotionEvent, e2: MotionEvent, dX: Float, dY: Float ): Boolean {
            val view = findViewById<ConstraintLayout>(R.id.Main_ConstraintLayout)
            view.scrollX += dX.toInt()
            view.scrollY += dY.toInt()

            return true
        }

        override fun onFling( e1: MotionEvent, e2: MotionEvent, vX: Float, vY: Float ): Boolean {
            val count = -0.1
            val dX = vX * count
            val dY = vY * count

            return onScroll(e1,e2,dX.toFloat(),dY.toFloat())
        }
    }
}


