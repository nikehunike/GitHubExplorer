package com.example.githubexplorer.ui.detail

import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubexplorer.data.remote.dto.RepoDetailDto
import com.example.githubexplorer.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoDetailViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    companion object {
        private const val TAG = "RepoDetailViewModel"
    }

    private val _uiState = MutableStateFlow<RepoDetailUiState>(RepoDetailUiState.Loading)
    val uiState: StateFlow<RepoDetailUiState> = _uiState.asStateFlow()

    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked: StateFlow<Boolean> = _isBookmarked.asStateFlow()

    private var currentDetail: RepoDetailDto? = null

    fun loadRepo(owner: String, repo: String) {
        viewModelScope.launch {
            _uiState.value = RepoDetailUiState.Loading
            try {
                val repoDeferred = async { repository.getRepoDetail(owner, repo) }
                val readmeDeferred = async { repository.getReadme(owner, repo) }

                val detailResult = repoDeferred.await()
                val readmeResult = readmeDeferred.await()

                val readmeMarkdown = readmeResult.getOrNull()?.let { decodeBase64(it.content) }

                detailResult
                    .onSuccess { detail ->
                        currentDetail = detail
                        _uiState.value = RepoDetailUiState.Success(detail, readmeMarkdown)
                        Log.i(TAG, "仓库详情加载成功: ${detail.fullName}")
                        // 拿到 id 后查收藏状态
                        checkBookmarkStatus(detail.id)
                    }
                    .onFailure { e ->
                        _uiState.value = RepoDetailUiState.Error(
                            e.message ?: "加载仓库详情失败"
                        )
                        Log.e(TAG, "仓库详情加载失败", e)
                    }
            } catch (e: Exception) {
                _uiState.value = RepoDetailUiState.Error(e.message ?: "未知错误")
                Log.e(TAG, "加载失败", e)
            }
        }
    }

    fun toggleBookmark() {
        val detail = currentDetail ?: return
        viewModelScope.launch {
            if (_isBookmarked.value) {
                repository.removeBookmark(detail.id)
                _isBookmarked.value = false
            } else {
                repository.addBookmark(detail)
                _isBookmarked.value = true
            }
        }
    }

    private suspend fun checkBookmarkStatus(repoId: Long) {
        _isBookmarked.value = repository.isBookmarked(repoId)
    }

    private fun decodeBase64(content: String): String {
        return try {
            val cleanContent = content.replace("\n", "").replace("\r", "")
            String(Base64.decode(cleanContent, Base64.DEFAULT))
        } catch (e: Exception) {
            Log.e(TAG, "Base64 解码失败", e)
            "README 内容无法解码"
        }
    }
}
