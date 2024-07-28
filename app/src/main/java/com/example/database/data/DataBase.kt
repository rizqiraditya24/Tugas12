package com.example.database.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Friend::class],
    version = 1
)

abstract class DataBase : RoomDatabase(){
    abstract fun friendDao(): FriendDao

    companion object{
        @Volatile
        private var INSTANCE : DataBase? = null

        fun getInstance(context: Context): DataBase{
            val tempInstance = INSTANCE
            if (tempInstance !=null) {
                return tempInstance
            }

            val instance = Room.databaseBuilder(
                context.applicationContext,
                DataBase::class.java,
                "my_database"
            ).fallbackToDestructiveMigration().build()

            INSTANCE = instance
            return instance
        }
    }
}