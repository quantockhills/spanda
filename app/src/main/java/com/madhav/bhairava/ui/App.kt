package com.madhav.bhairava.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun App(deepLink: String?, onDeepLinkHandled: () -> Unit, onThemeChanged: () -> Unit) {
    val nav = rememberNavController()

    LaunchedEffect(deepLink) {
        val route = deepLink ?: return@LaunchedEffect
        nav.navigate(route) { launchSingleTop = true }
        onDeepLinkHandled()
    }

    NavHost(navController = nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onOpenSivabodha = { nav.navigate("sivabodha") },
                onOpenAmrta = { nav.navigate("amrta") },
                onOpenGita = { nav.navigate("gita") },
                onOpenRoute = { route -> nav.navigate(route) { launchSingleTop = true } },
                onOpenSettings = { nav.navigate("settings") },
                onOpenFavorites = { nav.navigate("favorites") }
            )
        }
        composable("sivabodha") {
            SivabodhaListScreen(
                onBack = { nav.popBackStack() },
                onOpen = { i -> nav.navigate("sivabodha/$i") }
            )
        }
        composable(
            "sivabodha/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { entry ->
            StanzaReaderScreen(
                index = entry.arguments?.getInt("index") ?: 0,
                onBack = { nav.popBackStack() }
            )
        }
        composable("amrta") {
            AmrtaListScreen(
                onBack = { nav.popBackStack() },
                onOpen = { i -> nav.navigate("amrta/$i") }
            )
        }
        composable(
            "amrta/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { entry ->
            BhairavaReaderScreen(
                index = entry.arguments?.getInt("index") ?: 0,
                onBack = { nav.popBackStack() }
            )
        }
        composable("gita") {
            GitaListScreen(
                onBack = { nav.popBackStack() },
                onOpen = { i -> nav.navigate("gita/$i") }
            )
        }
        composable(
            "gita/{chapter}/{verse}",
            arguments = listOf(
                navArgument("chapter") { type = NavType.IntType },
                navArgument("verse") { type = NavType.IntType }
            )
        ) { entry ->
            GitaVerseReaderScreen(
                chapterIndex = entry.arguments?.getInt("chapter") ?: 0,
                initialVerse = entry.arguments?.getInt("verse") ?: 0,
                onBack = { nav.popBackStack() }
            )
        }
        composable(
            "gita/{chapter}",
            arguments = listOf(navArgument("chapter") { type = NavType.IntType })
        ) { entry ->
            GitaVerseReaderScreen(
                chapterIndex = entry.arguments?.getInt("chapter") ?: 0,
                onBack = { nav.popBackStack() }
            )
        }
        composable("settings") {
            SettingsScreen(
                onBack = { nav.popBackStack() },
                onThemeChanged = onThemeChanged
            )
        }
        composable("favorites") {
            FavoritesScreen(
                onBack = { nav.popBackStack() },
                onOpenRoute = { route -> nav.navigate(route) { launchSingleTop = true } }
            )
        }
    }
}
