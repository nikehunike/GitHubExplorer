package com.example.githubexplorer.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReadmeDto(
    val name: String,
    val content: String,
    val encoding: String,
    val size: Int
)
