package com.example.DesktopOnAndroid

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import java.util.ArrayList

class AppsList_menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps_list_menu )

        val listView = findViewById<ListView>(R.id.apps_list)
        val appsList : ArrayList<Apps> = arrayListOf()
        val pm = packageManager
        val pmFlag = PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.MATCH_DISABLED_COMPONENTS
        val intent = Intent(Intent.ACTION_MAIN)
            .also {
                it.addCategory((Intent.CATEGORY_LAUNCHER)) }
        val installedApps = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .asSequence()
            .mapNotNull { it.activityInfo }
            .filter { it.packageName != this.packageName }
            .toList()
//        val installedApps = pm.getInstalledPackages(pmFlag)

        for(app in installedApps){
            val appInfo = pm.getApplicationInfo(app.packageName,0)
            val id :Long = 0
            val name = pm.getApplicationLabel(appInfo).toString()
            val packageName = app.packageName
            val marginTop = 0
            val marginLeft = 0
            val newApp = Apps(id,name,packageName,marginTop,marginLeft)
            appsList.add(newApp)
        }
        appsList.sortWith(Comparator{
            a,b -> a.name.compareTo(b.name)
        })

        val myAdapter = MyAdapter(this@AppsList_menu,appsList)
        listView.adapter = myAdapter

        listView.setOnItemClickListener { adapterView, view, position, id ->

            val item = adapterView.getItemAtPosition(position) as Apps
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra(APP_NAME,item.name)
            intent.putExtra(APP_PACKAGENAME,item.packageName)
            setResult(Activity.RESULT_OK,intent)
            finish()
            //val intent = pm.getLaunchIntentForPackage("com.example.DesktopOnAndroid")
            //startActivity(intent)
        }


    }


}

