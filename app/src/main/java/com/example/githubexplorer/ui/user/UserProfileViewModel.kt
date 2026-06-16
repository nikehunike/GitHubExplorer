package com.example.githubexplorer.ui.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubexplorer.data.remote.dto.RepoDto
import com.example.githubexplorer.data.remote.dto.UserDto
import com.example.githubexplorer.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UserProfileUiState {
    data object Loading : UserProfileUiState
    data class Success(
        val user: UserDto,
        val repos: List<RepoDto>
    ) : UserProfileUiState
    data class Error(val message: String) : UserProfileUiState
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    companion object {
        private const val TAG = "UserProfileViewModel"
    }

    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    fun loadUser(username: String) {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            try {
                val userDeferred = async { repository.getUser(username) }
                val reposDeferred = async { repository.getUserRepos(username) }

                val userResult = userDeferred.await()
                val reposResult = reposDeferred.await()

                val repos = reposResult.getOrDefault(emptyList())

                userResult
                    .onSuccess { user ->
                        _uiState.value = UserProfileUiState.Success(user, repos)
                        Log.i(TAG, "用户信息加载成功: ${user.login}")
                    }
                    .onFailure { e ->
                        _uiState.value = UserProfileUiState.Error(
                            e.message ?: "加载用户信息失败"
                        )
                        Log.e(TAG, "用户信息加载失败", e)
                    }
            } catch (e: Exception) {
                _uiState.value = UserProfileUiState.Error(e.message ?: "未知错误")
                Log.e(TAG, "加载失败", e)
            }
        }
    }
}
