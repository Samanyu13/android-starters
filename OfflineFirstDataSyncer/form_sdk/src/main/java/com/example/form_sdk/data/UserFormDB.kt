package com.example.form_sdk.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserProfile::class], version = 1)
abstract class UserFormDB : RoomDatabase() {
    abstract fun formDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserFormDB? = null

        fun getDatabase(context: Context): UserFormDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserFormDB::class.java,
                    "form_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}