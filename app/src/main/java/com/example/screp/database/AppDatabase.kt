package com.example.screp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.screp.dao.HeartRateDao
import com.example.screp.dao.PhotoDao
import com.example.screp.dao.RouteDao
import com.example.screp.dao.StepCountDao
import com.example.screp.data.HeartRate
import com.example.screp.data.Photo
import com.example.screp.data.Route
import com.example.screp.data.StepCount

@Database(entities = [(StepCount::class), (HeartRate::class), (Photo::class), (Route::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepCountDao(): StepCountDao
    abstract fun heartRateDao(): HeartRateDao
    abstract fun photoDao(): PhotoDao
    abstract fun RouteDao(): RouteDao


    companion object {
        private var database: AppDatabase? = null

        @Synchronized
        fun get(context: Context): AppDatabase {
            if (database == null) {
                database =
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "app.db"
                    ).build()
            }
            return database!!
        }
    }
}