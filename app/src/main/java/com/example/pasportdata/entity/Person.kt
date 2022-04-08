package com.example.pasportdata.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Person(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "full_name")
    var name: String,

    var surname: String,

    var middle_name: String,
    var region: String,
    var city: String,

    var address: String,

    var passport_date: String,

    var passport_end_date: String,
    var gender: String,
    var image: String,
    var numberPassport: String
) : Serializable