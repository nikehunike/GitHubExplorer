package com.example.githubexplorer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.githubexplorer.ui.bookmark.BookmarkScreen
import com.example.githubexplorer.ui.detail.RepoDetailScreen
import com.example.githubexplorer.ui.home.HomeScreen
import com.example.githubexplorer.ui.search.SearchScreen
import com.example.githubexplorer.ui.user.UserProfileScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home,
        modifier = modifier
    ) {
        composable<Route.Home> {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Route.Search) },
                onNavigateToBookmarks = { navController.navigate(Route.Bookmark) }
            )
        }

        composable<Route.Search> {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onRepoClick = { owner, repo ->
                    navController.navigate(Route.RepoDetail(owner, repo))
                },
                onUserClick = { username ->
                    navController.navigate(Route.UserProfile(username))
                }
            )
        }

        composable<Route.RepoDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.RepoDetail>()
            RepoDetailScreen(
                owner = route.owner,
                repo = route.repo,
                onBack = { navController.popBackStack() },
                onUserClick = { username ->
                    navController.navigate(Route.UserProfile(username))
                }
            )
        }

        composable<Route.Bookmark> {
            BookmarkScreen(
                onBack = { navController.popBackStack() },
                onRepoClick = { owner, repo ->
                    navController.navigate(Route.RepoDetail(owner, repo))
                },
                onUserClick = { username ->
                    navController.navigate(Route.UserProfile(username))
                }
            )
        }

        composable<Route.UserProfile> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.UserProfile>()
            UserProfileScreen(
                username = route.username,
                onBack = { navController.popBackStack() },
                onRepoClick = { owner, repo ->
                    navController.navigate(Route.RepoDetail(owner, repo))
                },
                onUserClick = { username ->
                    navController.navigate(Route.UserProfile(username))
                }
            )
        }
    }
}
