package com.example.githubexplorer.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubexplorer.data.remote.dto.RepoDto
import com.example.githubexplorer.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val repos: List<RepoDto>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    private  val TAG = "SearchViewModel"
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .filter { it.length >= 2 }
                .collectLatest { query -> search(query) }
        }
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _uiState.value = SearchUiState.Idle
        }
    }

    private suspend fun search(query: String) {
        Log.i(TAG, "开始搜索: $query")
        _uiState.value = SearchUiState.Loading
        repository.searchRepos(query)
            .onSuccess { repos ->
                Log.i(TAG, "搜索成功, 结果数: ${repos.size}")
                _uiState.value = if (repos.isEmpty()) {
                    SearchUiState.Error("没有找到相关仓库")
                } else {
                    SearchUiState.Success(repos)
                }
            }
            .onFailure { e ->
                Log.e(TAG, "搜索失败", e)
                _uiState.value = SearchUiState.Error(e.message ?: "网络出错了")
            }
    }
}
