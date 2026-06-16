package com.example.githubexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val forks: Int,
    val language: String?,
    val ownerLogin: String,
    val ownerAvatar: String,
    val htmlUrl: String,
    val bookmarkedAt: Long = System.currentTimeMillis()
)
