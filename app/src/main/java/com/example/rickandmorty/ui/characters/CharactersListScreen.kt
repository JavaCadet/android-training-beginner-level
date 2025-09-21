@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.rickandmorty.ui.characters

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rickandmorty.R
import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.ui.common.FailureScreen
import com.example.rickandmorty.ui.common.LoadingScreen
import com.example.rickandmorty.utils.UiState
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun CharactersListScreen(
    viewModel: CharactersListViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    updateTitle: (String) -> Unit,
    onCharacterClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyListState = rememberLazyListState()
    val title = stringResource(R.string.app_name)

    LaunchedEffect(Unit) {
        updateTitle(title)
        viewModel.getCharacters()
    }

    when (val result = uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Success -> CharactersList(
            characters = result.data,
            lazyListState = lazyListState,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            onLoadMore = { viewModel.getCharacters(true) },
            onCharacterClick = onCharacterClick
        )
        is UiState.Failure -> FailureScreen(
            message = result.message,
            onRetry = viewModel::getCharacters
        )
    }
}

@Composable
fun CharactersList(
    characters: List<Character>,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    lazyListState: LazyListState,
    onLoadMore: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Infinite scroll
    // TODO: Replace with Paging or Pull to refresh

    val lastVisibleItemIndex by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lastVisibleItemIndex }
            .distinctUntilChanged()
            .debounce(300)
            .collect { lastVisibleIndex ->
                val totalCount = lazyListState.layoutInfo.totalItemsCount
                if (lastVisibleIndex >= totalCount - 3) {
                    onLoadMore()
                }
            }
    }

    LazyColumn(
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small)),
        contentPadding = PaddingValues(dimensionResource(R.dimen.small)),
        modifier = modifier
    ) {
        items(
            items = characters,
            key = { characters -> characters.id }
        ) { character ->
            NeumorphismCharacterCard(
                character = character,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onCardClick = { onCharacterClick(character.id) },
                modifier = Modifier
            )
        }
    }
}

@Composable
fun NeumorphismCharacterCard(
    character: Character,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(
        targetValue = if (isPressed) 0.dp else dimensionResource(R.dimen.extra_small)
    )
    val blur by animateDpAsState(
        targetValue = if (isPressed) 0.dp else dimensionResource(R.dimen.extra_small)
    )

    Box(
        modifier = modifier.padding(dimensionResource(R.dimen.small))
    ) {
        // Light shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.small_image_size))
                .offset(
                    x = 0.dp,
                    y = -offset
                )
                .blur(
                    radius = blur,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .background(
                    color = Color.White.copy(alpha = .6f),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.medium))
                )
        )
        // Dark shadow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.small_image_size))
                .offset(
                    x = 0.dp,
                    y = offset
                )
                .blur(
                    radius = blur,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .background(
                    color = Color.Black.copy(alpha = .2f),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.medium))
                )
        )
        CharacterCard(
            character = character,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Disable ripple effect
                    onClick = onCardClick
                )
        )
    }
}

@Composable
fun CharacterCard(
    character: Character,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(dimensionResource(R.dimen.medium))

    Card(
        shape = cardShape,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    shape = cardShape,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceDim,
                            Color.Black.copy(alpha = .1f)
                        )
                    ))
                .border(
                    width = 1.dp,
                    shape = cardShape,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = .6f),
                            Color.Black.copy(alpha = .2f)
                        )
                    )
                )
        ) {
            with(sharedTransitionScope) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(character.image)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.broken_image),
                    contentDescription = null,
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = sharedTransitionScope.rememberSharedContentState(
                                key = "image-${character.id}"
                            ),
                            animatedVisibilityScope = animatedContentScope,
                            boundsTransform = { initial, target ->
                                tween(durationMillis = 300, easing = FastOutLinearInEasing)
                            }
                        )
                        .size(dimensionResource(R.dimen.small_image_size))
                )
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.medium))
                        .sharedElement(
                            sharedContentState = sharedTransitionScope.rememberSharedContentState(
                                key = "text-${character.id}"
                            ),
                            animatedVisibilityScope = animatedContentScope,
                            boundsTransform = { initial, target ->
                                tween(durationMillis = 300, easing = FastOutLinearInEasing)
                            }
                        )
                )
            }
        }
    }
}