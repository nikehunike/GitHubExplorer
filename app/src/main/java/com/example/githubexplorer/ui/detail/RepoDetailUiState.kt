package com.example.githubexplorer.ui.detail

import com.example.githubexplorer.data.remote.dto.RepoDetailDto

sealed interface RepoDetailUiState {
    data object Loading : RepoDetailUiState
    data class Success(
        val repo: RepoDetailDto,
        val readmeMarkdown: String?
    ) : RepoDetailUiState
    data class Error(val message: String) : RepoDetailUiState
}
