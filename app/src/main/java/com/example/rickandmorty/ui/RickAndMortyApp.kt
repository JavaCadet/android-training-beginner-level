@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.rickandmorty.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.rickandmorty.R
import com.example.rickandmorty.ui.character.CharacterDetailScreen
import com.example.rickandmorty.ui.characters.CharactersListScreen
import kotlinx.serialization.Serializable

@Composable
fun RickAndMortyApp() {
    val navController = rememberNavController()
    val appTitle = stringResource(R.string.app_name)
    var title by remember { mutableStateOf(appTitle) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = title,
                showUpButton = navController.previousBackStackEntry != null,
                onUpClick = navController::popBackStack
            )
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            startDestination = AppRoutes.ListOfCharacters,
            updateTitle = { newTitle -> title = newTitle },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceDim)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    showUpButton: Boolean,
    onUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (showUpButton) {
                IconButton(
                    onClick = onUpClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button_description)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            titleContentColor = MaterialTheme.colorScheme.inverseOnSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.inverseOnSurface
        ),
        modifier = modifier
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: AppRoutes,
    updateTitle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable<AppRoutes.ListOfCharacters> {
                CharactersListScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable,
                    updateTitle = updateTitle,
                    onCharacterClick = { characterId ->
                        navController.navigate(AppRoutes.CharacterDetail(characterId))
                    }
                )
            }
            composable<AppRoutes.CharacterDetail> { backStackEntry ->
                val args = backStackEntry.toRoute<AppRoutes.CharacterDetail>()
                CharacterDetailScreen(
                    characterId = args.characterId,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable,
                    updateTitle = updateTitle
                )
            }
        }
    }
}

sealed interface AppRoutes {
    @Serializable
    object ListOfCharacters : AppRoutes
    @Serializable
    data class CharacterDetail(val characterId: Int) : AppRoutes
}
