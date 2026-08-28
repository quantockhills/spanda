package com.madhav.bhairava.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.madhav.bhairava.sync.OneDriveSync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun App(deepLink: String?, onDeepLinkHandled: () -> Unit, onThemeChanged: () -> Unit) {
    val nav = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // silent auto-sync on launch when signed in
        if (OneDriveSync.isSignedIn(context)) {
            withContext(Dispatchers.IO) {
                try { OneDriveSync.syncNow(context) } catch (e: Exception) { }
            }
        }
    }

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
                onOpenSamvarta = { nav.navigate("samvarta") },
                onOpenRoute = { route -> nav.navigate(route) { launchSingleTop = true } },
                onOpenSettings = { nav.navigate("settings") },
                onOpenFavorites = { nav.navigate("favorites") },
                onOpenNotes = { nav.navigate("notes") }
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
        composable("samvarta") {
            SamvartaListScreen(
                onBack = { nav.popBackStack() },
                onOpen = { i -> nav.navigate("samvarta/$i") }
            )
        }
        composable(
            "samvarta/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { entry ->
            SamvartaReaderScreen(
                index = entry.arguments?.getInt("index") ?: 0,
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
        composable("notes") {
            NotesScreen(
                onBack = { nav.popBackStack() },
                onOpenRoute = { route -> nav.navigate(route) { launchSingleTop = true } }
            )
        }
    }
}
