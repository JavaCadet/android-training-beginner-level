package com.example.rickandmorty.ui.character

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

class CharacterDetailViewModelTest {
    private lateinit var repository: RickAndMortyRepository
    private lateinit var viewModel: CharacterDetailViewModel
    private val character = Character(
        id = 1,
        name = "Rick Sanchez",
        status = CharacterStatus.ALIVE,
        species = "Human",
        type = "",
        gender = CharacterGender.MALE,
        origin = Location(name = "Earth (C-137)", url = "https://rickandmortyapi.com/api/location/1"),
        location = Location(name = "Earth (Replacement Dimension)", url = "https://rickandmortyapi.com/api/location/20"),
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
    )

    @BeforeTest
    fun setup() {
        repository = mock()
        viewModel = CharacterDetailViewModel(repository)
    }

    @Test
    fun `Initial state is Loading`() = runTest {
        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCharacterById emits Success when Repository returns data`() = runTest {
        whenever(repository.getCharacterById(character.id))
            .thenReturn(ApiResult.Success(character))

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())

            viewModel.getCharacterById(character.id)
            assertEquals(UiState.Success(character), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCharacterById emits Failure when Repository fails`() = runTest {
        whenever(repository.getCharacterById(99))
            .thenReturn(ApiResult.Failure("Not found"))

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())

            viewModel.getCharacterById(99)
            assertEquals(UiState.Failure("Not found"), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCharacterById emits different states on repeated calls`() = runTest {
        whenever(repository.getCharacterById(character.id))
            .thenReturn(ApiResult.Failure("Network error"))
            .thenReturn(ApiResult.Success(character))

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())

            // First call - failure
            viewModel.getCharacterById(character.id)
            assertEquals(UiState.Failure("Network error"), awaitItem())

            // Second call - success
            viewModel.getCharacterById(character.id)
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Success(character), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCharacterById passes correct ID value`() = runTest {
        whenever(repository.getCharacterById(character.id))
            .thenReturn(ApiResult.Success(character))

        viewModel.getCharacterById(character.id)

        val captor = argumentCaptor<Int>()
        verify(repository).getCharacterById(captor.capture())
        assertEquals(character.id, captor.firstValue)
    }
}