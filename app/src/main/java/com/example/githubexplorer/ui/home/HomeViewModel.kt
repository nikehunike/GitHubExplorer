package com.example.githubexplorer.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubexplorer.data.remote.dto.RepoDto
import com.example.githubexplorer.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val repos: List<RepoDto>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTrending()
    }

    fun loadTrending() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            repository.getTrendingRepos()
                .onSuccess { repos ->
                    _uiState.value = HomeUiState.Success(repos)
                    Log.i(TAG, "热门仓库加载成功: ${repos.size} 个")
                }
                .onFailure { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "加载失败")
                    Log.e(TAG, "热门仓库加载失败", e)
                }
        }
    }
}
