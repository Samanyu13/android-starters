package com.example.shoppingapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CartItem::class, OrderHistory::class],
    version = 1,
    exportSchema = false
)
abstract class ShopDatabase : RoomDatabase() {
    abstract fun shopDao(): ShopDao
}