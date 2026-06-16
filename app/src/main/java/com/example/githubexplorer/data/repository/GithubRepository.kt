package com.example.githubexplorer.data.repository

import com.example.githubexplorer.data.remote.GitHubApi
import com.example.githubexplorer.data.remote.dto.RepoDetailDto
import com.example.githubexplorer.data.remote.dto.RepoDto
import com.example.githubexplorer.data.remote.dto.ReadmeDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun searchRepos(query: String): Result<List<RepoDto>> = runCatching {
        api.searchRepos(query).items
    }

    suspend fun getRepoDetail(owner: String, repo: String): Result<RepoDetailDto> = runCatching {
        api.getRepoDetail(owner, repo)
    }

    suspend fun getReadme(owner: String, repo: String): Result<ReadmeDto> = runCatching {
        api.getReadme(owner, repo)
    }
}
