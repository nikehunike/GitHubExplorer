package com.example.githubexplorer.data.remote

import com.example.githubexplorer.data.remote.dto.RepoDetailDto
import com.example.githubexplorer.data.remote.dto.RepoDto
import com.example.githubexplorer.data.remote.dto.RepoSearchResponse
import com.example.githubexplorer.data.remote.dto.ReadmeDto
import com.example.githubexplorer.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {

    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 20
    ): RepoSearchResponse

    @GET("repos/{owner}/{repo}")
    suspend fun getRepoDetail(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): RepoDetailDto

    @GET("repos/{owner}/{repo}/readme")
    suspend fun getReadme(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): ReadmeDto

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): UserDto

    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("sort") sort: String = "updated",
        @Query("per_page") perPage: Int = 30
    ): List<RepoDto>
}
