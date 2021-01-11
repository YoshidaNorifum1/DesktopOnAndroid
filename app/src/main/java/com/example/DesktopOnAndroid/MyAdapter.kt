package com.example.DesktopOnAndroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList
class MyAdapter(context: Context, val appList: ArrayList<Apps>) : BaseAdapter() {
    val layoutInflater : LayoutInflater
    val pm = context.packageManager

    init{
        layoutInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return appList.count()
    }

    override fun getView(p0: Int, p1: View? , p2: ViewGroup?): View? {
        val item = getItem(p0) as Apps
        val convertView = layoutInflater.inflate(R.layout.activity_apps_list_menu,p2,false)

        val imageView = convertView.findViewById<ImageView>(R.id.app_icon)
        imageView.setImageDrawable(pm.getApplicationIcon(item.packageName))

        val textView = convertView.findViewById<TextView>(R.id.name)
        textView.text = item.name
        return convertView
    }

    override fun getItem(p0:Int): Apps {
        return appList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return appList[p0].id.toLong()
    }





}