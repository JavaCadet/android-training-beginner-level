package com.example.rickandmorty.ui.characters

import app.cash.turbine.test
import com.example.rickandmorty.data.model.Character
import com.example.rickandmorty.data.model.CharacterGender
import com.example.rickandmorty.data.model.CharacterStatus
import com.example.rickandmorty.data.model.Location
import com.example.rickandmorty.data.repository.RickAndMortyRepository
import com.example.rickandmorty.utils.ApiResult
import com.example.rickandmorty.utils.UiState
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CharactersListViewModelTest {

    private lateinit var repository: RickAndMortyRepository
    private lateinit var viewModel: CharactersListViewModel
    private val characters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            status = CharacterStatus.ALIVE,
            species = "Human",
            type = "",
            gender = CharacterGender.MALE,
            origin = Location(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
            location = Location(name = "Earth (Replacement Dimension)", url = "https://rickandmortyapi.com/api/location/20"),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        ),
        Character(
            id = 2,
            name = "Morty Smith",
            status = CharacterStatus.ALIVE,
            species = "Human",
            type = "",
            gender = CharacterGender.MALE,
            origin = Location(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
            location = Location(name = "Earth (Replacement Dimension)", url = "https://rickandmortyapi.com/api/location/20"),
            image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
        )
    )

    @BeforeTest
    fun setup() {
        repository = mock()
        viewModel = CharactersListViewModel(repository)
    }

    @Test
    fun `Initial state is Loading`() = runTest {
        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCharacters emits Success when Repository returns data`() = runTest {
        whenever(repository.getCharacters())
            .thenReturn(ApiResult.Success(characters))

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())

            viewModel.getCharacters()
            assertEquals(UiState.Success(characters), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCharacters emits Failure when Repository fails`() = runTest {
        whenever(repository.getCharacters())
            .thenReturn(ApiResult.Failure("Error occurred"))

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())

            viewModel.getCharacters()
            assertEquals(UiState.Failure("Error occurred"), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCharacters emits Success when Repository returns empty list`() = runTest {
        whenever(repository.getCharacters())
            .thenReturn(ApiResult.Success(emptyList()))

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())

            viewModel.getCharacters()
            assertEquals(UiState.Success(emptyList()), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCharacters emits different states on repeated calls`() = runTest {
        whenever(repository.getCharacters())
            .thenReturn(ApiResult.Failure("Network error"))
            .thenReturn(ApiResult.Success(characters))

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())

            // First call - failure
            viewModel.getCharacters()
            assertEquals(UiState.Failure("Network error"), awaitItem())

            // Second call - success
            viewModel.getCharacters()
            assertEquals(UiState.Success(characters), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCharacters passes correct loadMore value`() = runTest {
        whenever(repository.getCharacters(true))
            .thenReturn(ApiResult.Success(characters))

        viewModel.getCharacters(true)

        val captor = argumentCaptor<Boolean>()
        verify(repository).getCharacters(captor.capture())
        assertEquals(true, captor.firstValue)
    }
}