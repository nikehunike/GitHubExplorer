package com.example.githubexplorer.data.remote

import com.example.githubexplorer.data.remote.dto.RepoSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {

    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 20
    ): RepoSearchResponse
}
