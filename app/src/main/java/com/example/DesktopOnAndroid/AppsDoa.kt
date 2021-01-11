package com.example.DesktopOnAndroid

import androidx.room.*

@Dao
interface AppsDoa {
    @Insert
    fun insert(apps: Apps): Long

    @Update
    fun update(apps: Apps)

    @Delete
    fun delete(apps: Apps)

    @Query("select * from apps")
    fun getAll(): List<Apps>

    @Query("select * from apps where id = :id")
    fun getAppbyId(id: Int): Apps

    @Query("delete from apps where id = :id")
    fun deleteAppbyId(id: Int)

    @Query("delete from apps")
    fun deleteAll()

}