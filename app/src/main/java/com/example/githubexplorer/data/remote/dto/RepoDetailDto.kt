package com.example.githubexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetailDto(
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val owner: OwnerDto,
    val description: String? = null,
    @SerialName("stargazers_count") val stars: Int = 0,
    @SerialName("forks_count") val forks: Int = 0,
    @SerialName("subscribers_count") val watchers: Int = 0,
    val language: String? = null,
    val topics: List<String> = emptyList(),
    @SerialName("html_url") val htmlUrl: String = "",
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    val license: LicenseDto? = null
)

@Serializable
data class LicenseDto(
    val name: String
)
