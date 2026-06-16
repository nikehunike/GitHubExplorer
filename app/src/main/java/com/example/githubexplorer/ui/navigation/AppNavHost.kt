package com.example.githubexplorer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.githubexplorer.ui.home.HomeScreen
import com.example.githubexplorer.ui.search.SearchScreen

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
                onNavigateToSearch = {
                    navController.navigate(Route.Search)
                }
            )
        }

        composable<Route.Search> {
            SearchScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
