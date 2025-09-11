package com.example.rickandmorty.ui.characters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rickandmorty.R
import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.ui.common.FailureScreen
import com.example.rickandmorty.ui.common.LoadingScreen
import com.example.rickandmorty.utils.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun CharactersListScreen(
    viewModel: CharactersListViewModel = hiltViewModel(),
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
            onLoadMore = { viewModel.getCharacters(true) },
            onCharacterClick = onCharacterClick
        )
        is UiState.Failure -> FailureScreen(
            message = result.message,
            onRetry = viewModel::getCharacters
        )
    }
}
@OptIn(FlowPreview::class)
@Composable
fun CharactersList(
    characters: List<Character>,
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
        items(characters, { characters -> characters.id }) { character ->
            CharacterCard(
                character = character,
                onCardClick = onCharacterClick,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun CharacterCard(
    character: Character,
    onCardClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier.padding(dimensionResource(R.dimen.small))
    ) {
        // Simulate elevated state with drop shadows
        if (!isPressed) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.small_image_size))
                    .dropShadow( // Top highlight
                        shape = RoundedCornerShape(dimensionResource(R.dimen.medium)),
                        shadow = Shadow(
                            color = Color.White.copy(alpha = 0.8f),
                            radius = dimensionResource(R.dimen.small),
                            offset = DpOffset(
                                x = -dimensionResource(R.dimen.extra_small),
                                y = -dimensionResource(R.dimen.extra_small)
                            )
                        )
                    )
                    .dropShadow( // Bottom shadow
                        shape = RoundedCornerShape(dimensionResource(R.dimen.medium)),
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.2f),
                            radius = dimensionResource(R.dimen.small),
                            offset = DpOffset(
                                x = dimensionResource(R.dimen.extra_small),
                                y = dimensionResource(R.dimen.extra_small)
                            )
                        )
                    )
            )
        }

        Card(
            shape = RoundedCornerShape(dimensionResource(R.dimen.medium)),
            elevation = CardDefaults.cardElevation(),
            modifier = modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Disable ripple effect
                    onClick = { onCardClick(character.id) }
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(character.image)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.loading_img),
                    error = painterResource(R.drawable.broken_image),
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.small_image_size))
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
                )
            }
        }

        // Simulate clicked state with inner shadows
        if (isPressed) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.small_image_size))
                    .innerShadow( // Top shadow
                        shape = RoundedCornerShape(dimensionResource(R.dimen.medium)),
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.2f),
                            radius = dimensionResource(R.dimen.small),
                            offset = DpOffset(
                                x = dimensionResource(R.dimen.extra_small),
                                y = dimensionResource(R.dimen.extra_small)
                            )
                        )
                    )
                    .innerShadow( // Bottom highlight
                        shape = RoundedCornerShape(dimensionResource(R.dimen.medium)),
                        shadow = Shadow(
                            color = Color.White.copy(alpha = 0.6f),
                            radius = dimensionResource(R.dimen.small),
                            offset = DpOffset(
                                x = -dimensionResource(R.dimen.extra_small),
                                y = -dimensionResource(R.dimen.extra_small)
                            )
                        )
                    )
            )
        }
    }
}