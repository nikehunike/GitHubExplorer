package com.example.githubexplorer.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt 模块，后续添加:
 * - OkHttpClient + Retrofit 实例
 * - Room Database + DAO
 * - Repository 绑定
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
