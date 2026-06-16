package com.example.githubexplorer.di

import android.content.Context
import androidx.room.Room
import com.example.githubexplorer.data.local.AppDatabase
import com.example.githubexplorer.data.local.BookmarkDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "github_explorer.db")
            .build()

    @Provides
    fun provideBookmarkDao(database: AppDatabase): BookmarkDao =
        database.bookmarkDao()
}
