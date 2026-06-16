package com.example.githubexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val login: String,
    val id: Long,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("html_url") val htmlUrl: String = "",
    val name: String? = null,
    val bio: String? = null,
    @SerialName("public_repos") val publicRepos: Int = 0,
    val followers: Int = 0,
    val following: Int = 0
)
