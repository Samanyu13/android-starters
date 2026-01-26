package com.example.shoppingapp.module

import android.content.Context
import androidx.room.Room
import com.example.shoppingapp.data.local.ShopDao
import com.example.shoppingapp.data.local.ShopDatabase
import com.example.shoppingapp.data.repo.ShopRepository
import com.example.shoppingapp.data.repo.ShopRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ShopDatabase {
        return Room.databaseBuilder(
            context,
            ShopDatabase::class.java,
            "shop_db"
        ).build()
    }

    @Provides
    fun provideShopDao(db: ShopDatabase): ShopDao = db.shopDao()

    @Provides
    @Singleton
    fun provideRepository(dao: ShopDao): ShopRepository {
        return ShopRepositoryImpl(dao)
    }
}