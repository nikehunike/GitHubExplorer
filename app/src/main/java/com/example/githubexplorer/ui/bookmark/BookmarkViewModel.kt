package com.example.githubexplorer.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubexplorer.data.local.entity.BookmarkEntity
import com.example.githubexplorer.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    repository: GithubRepository
) : ViewModel() {

    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.getBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
