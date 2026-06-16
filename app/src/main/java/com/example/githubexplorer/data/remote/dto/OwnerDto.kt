package com.example.githubexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OwnerDto(
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String
)
