package com.example.rickandmorty.ui.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
        modifier = modifier.padding(dimensionResource(R.dimen.small))
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
    Card(
        shape = RoundedCornerShape(dimensionResource(R.dimen.medium)),
        elevation = CardDefaults.cardElevation(),
        onClick = { onCardClick(character.id) },
        modifier = modifier.fillMaxWidth()
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
            Spacer(modifier.width(dimensionResource(R.dimen.medium)))
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}