package com.example.githubexplorer.ui.navigation

import kotlinx.serialization.Serializable

/**
 * 路由定义，使用 type-safe navigation
 * 后续逐步添加 Search、RepoDetail、UserProfile、Bookmark
 */
@Serializable
sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object Search : Route

    @Serializable
    data class RepoDetail(val owner: String, val repo: String) : Route

    @Serializable
    data object Bookmark : Route

    @Serializable
    data class UserProfile(val username: String) : Route
}
