package com.example.githubexplorer.data.repository

import com.example.githubexplorer.data.remote.GitHubApi
import com.example.githubexplorer.data.remote.dto.RepoDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun searchRepos(query: String): Result<List<RepoDto>> = runCatching {
        api.searchRepos(query).items
    }
}
