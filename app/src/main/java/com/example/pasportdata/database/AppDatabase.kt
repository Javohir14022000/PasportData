package com.example.pasportdata.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pasportdata.AddPersonFragment
import com.example.pasportdata.dao.PersonDao
import com.example.pasportdata.entity.Person

@Database(
    entities = [Person::class], version = 1
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun personDao(): PersonDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "my.db")
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}