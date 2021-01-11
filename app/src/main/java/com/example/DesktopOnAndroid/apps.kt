package com.example.DesktopOnAndroid

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Apps (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val packageName: String,
    val topMargin: Int,
    val leftMargin: Int
)
