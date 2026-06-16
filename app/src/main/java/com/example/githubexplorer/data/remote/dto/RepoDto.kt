package com.example.githubexplorer.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDto(
    val id: Long,
    val name: String,
    @SerialName("full_name") val fullName: String,
    val owner: OwnerDto,
    @SerialName("html_url") val htmlUrl: String,
    val description: String? = null,
    @SerialName("stargazers_count") val stars: Int = 0,
    @SerialName("forks_count") val forks: Int = 0,
    val language: String? = null,
    val topics: List<String> = emptyList()
)
