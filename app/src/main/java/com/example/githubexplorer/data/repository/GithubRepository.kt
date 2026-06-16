package com.example.githubexplorer.data.repository

import com.example.githubexplorer.data.local.BookmarkDao
import com.example.githubexplorer.data.local.entity.BookmarkEntity
import com.example.githubexplorer.data.remote.GitHubApi
import com.example.githubexplorer.data.remote.dto.RepoDetailDto
import com.example.githubexplorer.data.remote.dto.RepoDto
import com.example.githubexplorer.data.remote.dto.ReadmeDto
import com.example.githubexplorer.data.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepository @Inject constructor(
    private val api: GitHubApi,
    private val bookmarkDao: BookmarkDao
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

    suspend fun isBookmarked(repoId: Long): Boolean = bookmarkDao.isBookmarked(repoId)

    fun getBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAll()

    suspend fun addBookmark(repo: RepoDetailDto) {
        bookmarkDao.insert(
            BookmarkEntity(
                id = repo.id,
                name = repo.name,
                fullName = repo.fullName,
                description = repo.description,
                stars = repo.stars,
                forks = repo.forks,
                language = repo.language,
                ownerLogin = repo.owner.login,
                ownerAvatar = repo.owner.avatarUrl,
                htmlUrl = repo.htmlUrl
            )
        )
    }

    suspend fun removeBookmark(repoId: Long) = bookmarkDao.delete(repoId)

    suspend fun getUser(username: String): Result<UserDto> = runCatching {
        api.getUser(username)
    }

    suspend fun getUserRepos(username: String): Result<List<RepoDto>> = runCatching {
        api.getUserRepos(username)
    }
}
