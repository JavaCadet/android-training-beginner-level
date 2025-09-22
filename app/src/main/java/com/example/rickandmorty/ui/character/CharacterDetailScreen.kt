@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.rickandmorty.ui.character

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rickandmorty.R
import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.data.model.CharacterStatus
import com.example.rickandmorty.ui.common.FailureScreen
import com.example.rickandmorty.ui.common.LoadingScreen
import com.example.rickandmorty.utils.UiState

@Composable
fun CharacterDetailScreen(
    characterId: Int,
    viewModel: CharacterDetailViewModel = hiltViewModel(),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    updateTitle: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(characterId) {
        viewModel.getCharacterById(characterId)
    }

    when (val result = uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Success -> CharacterDetail(
            character = result.data,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
            updateTitle = updateTitle
        )
        is UiState.Failure -> FailureScreen(
            result.message,
            { viewModel.getCharacterById(characterId) }
        )
    }
}

@Composable
fun CharacterDetail(
    character: Character,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    updateTitle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(character) {
        updateTitle(character.name)
    }

    with(sharedTransitionScope) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier.padding(dimensionResource(R.dimen.medium))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(character.image)
                    .crossfade(true)
                    .build(),
                error = painterResource(R.drawable.broken_image),
                placeholder = painterResource(R.drawable.loading_img),
                contentDescription = null,
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = sharedTransitionScope.rememberSharedContentState(
                            key = "image-${character.id}"
                        ),
                        animatedVisibilityScope = animatedContentScope,
                        boundsTransform = { initial, target ->
                            tween(durationMillis = 300, easing = LinearOutSlowInEasing)
                        }
                    )
                    .size(dimensionResource(R.dimen.large_image_size))
                    .clip(CircleShape)
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.medium)))
            Text(
                text = character.name,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .sharedElement(
                        sharedContentState = sharedTransitionScope.rememberSharedContentState(
                            key = "text-${character.id}"
                        ),
                        animatedVisibilityScope = animatedContentScope,
                        boundsTransform = { initial, target ->
                            tween(durationMillis = 300, easing = LinearOutSlowInEasing)
                        }
                    )
                    .width(dimensionResource(R.dimen.large_image_size))
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.medium)))
            CharacterStatusInfo(character.status)
            Spacer(Modifier.height(dimensionResource(R.dimen.medium)))
            Column(Modifier.width(dimensionResource(R.dimen.large_image_size))) {
                CharacterInfo(label = "Species", value = character.species)
                CharacterInfo(label = "Gender", value = character.gender.value)
            }
        }
    }
}

@Composable
fun CharacterInfo(
    label: String,
    value: String
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}

@Composable
fun CharacterStatusInfo(
    status: CharacterStatus
) {
    val color = when (status) {
        CharacterStatus.ALIVE -> Color(0xFF4CAF50)
        CharacterStatus.DEAD -> Color(0xFFF44336)
        CharacterStatus.UNKNOWN -> Color.Gray
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.2f))
            .padding(
                horizontal = dimensionResource(R.dimen.medium),
                vertical = dimensionResource(R.dimen.small)
            )
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(R.dimen.medium))
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.small)))
        Text(
            text = status.value,
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}
