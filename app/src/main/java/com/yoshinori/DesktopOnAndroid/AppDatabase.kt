package com.yoshinori.DesktopOnAndroid

import androidx.room.*

@Database(entities = arrayOf(Apps::class), version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun appsDoa(): AppsDoa
}