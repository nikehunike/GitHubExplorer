package com.example.githubexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoSearchResponse(
    @SerialName("total_count") val totalCount: Int,
    val items: List<RepoDto>
)
