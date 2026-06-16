package com.example.githubexplorer.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.mikepenz.markdown.m3.Markdown

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RepoDetailScreen(
    owner: String,
    repo: String,
    onBack: () -> Unit,
    onUserClick: (String) -> Unit = {},
    viewModel: RepoDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isBookmarked by viewModel.isBookmarked.collectAsStateWithLifecycle()

    LaunchedEffect(owner, repo) {
        viewModel.loadRepo(owner, repo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$owner/$repo", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark() }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Star
                                          else Icons.Filled.StarBorder,
                            contentDescription = if (isBookmarked) "取消收藏" else "收藏",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is RepoDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is RepoDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }

            is RepoDetailUiState.Success -> {
                val detail = state.repo
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = detail.owner.avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = detail.fullName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = detail.owner.login,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (!detail.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = detail.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        StatItem("Stars", formatCount(detail.stars))
                        StatItem("Forks", formatCount(detail.forks))
                        StatItem("Watchers", formatCount(detail.watchers))
                        if (!detail.language.isNullOrBlank()) {
                            StatItem("Language", detail.language)
                        }
                    }

                    if (detail.topics.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            detail.topics.forEach { topic ->
                                Text(
                                    text = topic,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (detail.license != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "License: ${detail.license.name}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (!state.readmeMarkdown.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "README.md",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Markdown(content = state.readmeMarkdown)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatCount(count: Int): String = when {
    count >= 1000 -> "${count / 1000}.${(count % 1000) / 100}k"
    else -> count.toString()
}
